package org.patinanetwork.codepulse.common.potd.leetcode;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.Map;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.springframework.stereotype.Component;

/**
 * A minimal, self-contained client that fetches only LeetCode's public "Problem of the Day" via LeetCode's public
 * GraphQL endpoint. This does not require authentication (unlike the old submission-polling {@code LeetcodeClient}),
 * so it does not depend on {@code LeetcodeAuthStealer} or any of the removed submission-polling machinery.
 */
@Component
public class LeetcodePotdClient {

    private static final String GRAPHQL_ENDPOINT = "https://leetcode.com/graphql";

    private static final String QUERY =
            """
            #graphql
            query questionOfToday {
                activeDailyCodingChallengeQuestion {
                    question {
                        titleSlug
                        title
                        difficulty
                    }
                }
            }
            """;

    private final HttpClient client;
    private final ObjectMapper mapper;

    public LeetcodePotdClient() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    private String body() {
        try {
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("query", QUERY);
            return mapper.writeValueAsString(requestBodyMap);
        } catch (JsonProcessingException e) {
            throw new LeetcodePotdClientException("Error building the request body", e);
        }
    }

    @Retry(name = "leetcodePotdClient")
    @CircuitBreaker(name = "leetcodePotdClient")
    public LeetcodePotd getPotd() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GRAPHQL_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(body()))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();

            if (statusCode != 200) {
                throw new LeetcodePotdClientException("API returned status " + statusCode + ": " + responseBody);
            }

            JsonNode node = mapper.readTree(responseBody);
            JsonNode baseNode =
                    node.path("data").path("activeDailyCodingChallengeQuestion").path("question");

            String titleSlug = baseNode.path("titleSlug").asText();
            String title = baseNode.path("title").asText();
            QuestionDifficulty difficulty =
                    QuestionDifficulty.valueOf(baseNode.path("difficulty").asText());

            return new LeetcodePotd(title, titleSlug, difficulty);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LeetcodePotdClientException("Thread interrupted", e);
        } catch (LeetcodePotdClientException e) {
            throw e;
        } catch (Exception e) {
            throw new LeetcodePotdClientException("Error fetching the POTD API", e);
        }
    }
}
