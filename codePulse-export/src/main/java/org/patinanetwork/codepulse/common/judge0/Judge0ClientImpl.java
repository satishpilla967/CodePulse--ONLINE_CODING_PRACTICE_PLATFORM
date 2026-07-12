package org.patinanetwork.codepulse.common.judge0;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.VisibleForTesting;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * REST client for a self-hosted Judge0 CE instance. Mirrors {@code LeetcodeClientImpl}'s choice of the JDK
 * {@link HttpClient} for blocking calls (virtual threads make blocking I/O idiomatic here, see application.yml).
 */
@Component
@Primary
@Slf4j
public class Judge0ClientImpl implements Judge0Client {

    private final String baseUrl;
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    @VisibleForTesting
    HttpClient client =
            HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    public Judge0ClientImpl(
            @Value("${judge0.base-url}") final String baseUrl, @Value("${judge0.api-key:}") final String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    private HttpRequest.Builder authedRequest(final URI uri) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10));
        if (StringUtils.hasText(apiKey)) {
            builder.header("X-Auth-Token", apiKey);
        }
        return builder;
    }

    private static String encode(final String value) {
        if (value == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    private String decode(final JsonNode node, final String field) {
        if (node == null || !node.hasNonNull(field)) {
            return null;
        }
        try {
            // Judge0 sometimes appends a trailing newline after the base64 payload itself (e.g.
            // "Mgo=\n" rather than "Mgo="). Java's strict Base64.getDecoder() throws
            // IllegalArgumentException on that trailing whitespace, unlike more lenient decoders
            // (e.g. Python's), so it must be trimmed before decoding.
            return new String(Base64.getDecoder().decode(node.get(field).asText().trim()));
        } catch (IllegalArgumentException e) {
            // A single malformed field must not fail the whole submission fetch: that would leave the
            // submission stuck PENDING/RUNNING forever, causing Judge0PollingService to retry it every 5s
            // in perpetuity and repeatedly trip the shared judge0Client circuit breaker for every user.
            log.warn("Judge0 returned a non-base64 '{}' field; treating as unavailable: {}", field, e.getMessage());
            return null;
        }
    }

    private ObjectNode buildSubmissionPayload(
            final int languageId, final String sourceCode, final String stdin, final String expectedOutput) {
        ObjectNode payload = mapper.createObjectNode();
        payload.put("language_id", languageId);
        payload.put("source_code", encode(sourceCode));
        if (stdin != null) {
            payload.put("stdin", encode(stdin));
        }
        if (expectedOutput != null) {
            payload.put("expected_output", encode(expectedOutput));
        }
        return payload;
    }

    @Override
    @Retry(name = "judge0Client")
    @CircuitBreaker(name = "judge0Client")
    public String createSubmission(
            final int languageId, final String sourceCode, final String stdin, final String expectedOutput) {
        try {
            ObjectNode payload = buildSubmissionPayload(languageId, sourceCode, stdin, expectedOutput);
            HttpRequest request = authedRequest(URI.create(baseUrl + "/submissions?base64_encoded=true&wait=false"))
                    .POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JsonNode body = mapper.readTree(response.body());
            return body.get("token").asText();
        } catch (Exception e) {
            log.error("Failed to create Judge0 submission", e);
            throw new RuntimeException("Failed to create Judge0 submission", e);
        }
    }

    @Override
    @Retry(name = "judge0Client")
    @CircuitBreaker(name = "judge0Client")
    public List<String> createSubmissionBatch(
            final int languageId, final String sourceCode, final List<String> stdins, final List<String> expectedOutputs) {
        try {
            ArrayNode submissions = mapper.createArrayNode();
            for (int i = 0; i < stdins.size(); i++) {
                submissions.add(buildSubmissionPayload(
                        languageId,
                        sourceCode,
                        stdins.get(i),
                        expectedOutputs != null && expectedOutputs.size() > i ? expectedOutputs.get(i) : null));
            }
            ObjectNode payload = mapper.createObjectNode();
            payload.set("submissions", submissions);

            HttpRequest request = authedRequest(URI.create(baseUrl + "/submissions/batch?base64_encoded=true"))
                    .POST(BodyPublishers.ofString(mapper.writeValueAsString(payload)))
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JsonNode body = mapper.readTree(response.body());

            List<String> tokens = new ArrayList<>();
            for (JsonNode node : body) {
                tokens.add(node.get("token").asText());
            }
            return tokens;
        } catch (Exception e) {
            log.error("Failed to create Judge0 submission batch", e);
            throw new RuntimeException("Failed to create Judge0 submission batch", e);
        }
    }

    private Judge0Submission parseSubmission(final JsonNode node) {
        JsonNode status = node.get("status");
        return Judge0Submission.builder()
                .token(node.hasNonNull("token") ? node.get("token").asText() : null)
                .stdout(decode(node, "stdout"))
                .stderr(decode(node, "stderr"))
                .compileOutput(decode(node, "compile_output"))
                .statusId(status != null && status.hasNonNull("id") ? status.get("id").asInt() : null)
                .statusDescription(status != null && status.hasNonNull("description") ? status.get("description").asText() : null)
                .time(node.hasNonNull("time") ? node.get("time").asDouble() : null)
                .memory(node.hasNonNull("memory") ? node.get("memory").asLong() : null)
                .build();
    }

    @Override
    @Retry(name = "judge0Client")
    @CircuitBreaker(name = "judge0Client")
    public Judge0Submission getSubmission(final String token) {
        try {
            HttpRequest request = authedRequest(
                            URI.create(baseUrl + "/submissions/" + token + "?base64_encoded=true&fields=*"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            return parseSubmission(mapper.readTree(response.body()));
        } catch (Exception e) {
            log.error("Failed to fetch Judge0 submission {}", token, e);
            throw new RuntimeException("Failed to fetch Judge0 submission", e);
        }
    }

    @Override
    @Retry(name = "judge0Client")
    @CircuitBreaker(name = "judge0Client")
    public List<Judge0Submission> getSubmissionBatch(final List<String> tokens) {
        try {
            String joinedTokens = StringUtils.collectionToCommaDelimitedString(tokens);
            HttpRequest request = authedRequest(URI.create(
                            baseUrl + "/submissions/batch?tokens=" + joinedTokens + "&base64_encoded=true&fields=*"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            JsonNode body = mapper.readTree(response.body()).get("submissions");

            List<Judge0Submission> submissions = new ArrayList<>();
            if (body != null) {
                for (JsonNode node : body) {
                    submissions.add(parseSubmission(node));
                }
            }
            return submissions;
        } catch (Exception e) {
            log.error("Failed to fetch Judge0 submission batch", e);
            throw new RuntimeException("Failed to fetch Judge0 submission batch", e);
        }
    }
}
