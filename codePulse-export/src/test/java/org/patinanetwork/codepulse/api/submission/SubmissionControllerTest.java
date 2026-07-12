package org.patinanetwork.codepulse.api.submission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.patinanetwork.codepulse.common.db.models.question.Question;
import org.patinanetwork.codepulse.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codepulse.common.db.repos.question.QuestionRepository;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.dto.question.QuestionWithUserDto;
import org.patinanetwork.codepulse.config.TestProtector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestProtector.class)
@Slf4j
public class SubmissionControllerTest {

    /** The fixed admin user id that {@link TestProtector} authenticates requests as. */
    private static final String TEST_USER_ID = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";

    @LocalServerPort
    private int port;

    @Autowired
    private QuestionRepository questionRepository;

    private Question testQuestion;

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void setUpUri() {
        RestAssured.baseURI = "http://localhost";
    }

    @AfterAll
    void deleteTestQuestion() {
        if (testQuestion == null) {
            return;
        }
        questionRepository.deleteQuestionById(testQuestion.getId());
    }

    @Test
    @Order(1)
    void createTestQuestion() {
        Question question = Question.builder()
                .id(UUID.randomUUID().toString())
                .userId(TEST_USER_ID)
                .questionSlug("two-sum")
                .questionTitle("Two Sum")
                .questionDifficulty(QuestionDifficulty.EASY)
                .questionNumber(1)
                .questionLink("https://leetcode.com/problems/two-sum/")
                .acceptanceRate(50.0f)
                .createdAt(LocalDateTime.now())
                .submittedAt(LocalDateTime.now())
                .build();

        testQuestion = questionRepository.createQuestion(question);

        assertTrue(testQuestion != null, "Expected created question to not be null");
    }

    @Test
    @Order(2)
    void getSubmissionBySubmissionIdReturnsQuestion() {
        ApiResponder<QuestionWithUserDto> apiResponder = RestAssured.given()
                .when()
                .get("/api/leetcode/submission/{submissionId}", testQuestion.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<ApiResponder<QuestionWithUserDto>>() {});

        assertTrue(apiResponder.isSuccess(), "Expected the submission to be found");
        assertEquals(testQuestion.getId(), apiResponder.getPayload().getId());
        assertEquals("two-sum", apiResponder.getPayload().getQuestionSlug());
    }

    @Test
    @Order(3)
    void getSubmissionBySubmissionIdReturnsFailureWhenNotFound() {
        ApiResponder<QuestionWithUserDto> apiResponder = RestAssured.given()
                .when()
                .get("/api/leetcode/submission/{submissionId}", UUID.randomUUID().toString())
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<ApiResponder<QuestionWithUserDto>>() {});

        assertTrue(!apiResponder.isSuccess(), "Expected a non-existent submission id to fail");
    }
}
