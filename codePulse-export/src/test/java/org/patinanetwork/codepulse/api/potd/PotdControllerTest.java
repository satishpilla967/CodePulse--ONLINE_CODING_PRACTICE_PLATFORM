package org.patinanetwork.codepulse.api.potd;

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
import org.patinanetwork.codepulse.common.db.models.potd.POTD;
import org.patinanetwork.codepulse.common.db.repos.potd.POTDRepository;
import org.patinanetwork.codepulse.common.dto.ApiResponder;
import org.patinanetwork.codepulse.common.dto.potd.PotdDto;
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
public class PotdControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private POTDRepository potdRepository;

    private POTD testPotd;

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void setUpUri() {
        RestAssured.baseURI = "http://localhost";
    }

    @AfterAll
    void deleteTestPotd() {
        if (testPotd == null) {
            return;
        }
        potdRepository.deletePOTD(testPotd.getId());
    }

    @Test
    @Order(1)
    void createTestPotd() {
        POTD potd = POTD.builder()
                .id(UUID.randomUUID().toString())
                .title("Two Sum")
                .slug("two-sum")
                .multiplier(1.5f)
                .createdAt(LocalDateTime.now())
                .build();

        testPotd = potdRepository.createPOTD(potd);

        assertTrue(testPotd != null, "Expected created POTD to not be null");
    }

    @Test
    @Order(2)
    void getCurrentPotdReturnsMostRecentlyCreated() {
        ApiResponder<PotdDto> apiResponder = RestAssured.given()
                .when()
                .get("/api/leetcode/potd")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<ApiResponder<PotdDto>>() {});

        assertTrue(apiResponder.isSuccess(), "Expected a POTD to be found");
        assertEquals(testPotd.getSlug(), apiResponder.getPayload().getSlug());
    }

    @Test
    @Order(3)
    void getCurrentPotdEmbedReturnsSameData() {
        ApiResponder<PotdDto> apiResponder = RestAssured.given()
                .when()
                .get("/api/leetcode/potd/embed")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<ApiResponder<PotdDto>>() {});

        assertTrue(apiResponder.isSuccess(), "Expected the embed endpoint to also find a POTD");
        assertEquals(testPotd.getSlug(), apiResponder.getPayload().getSlug());
    }
}
