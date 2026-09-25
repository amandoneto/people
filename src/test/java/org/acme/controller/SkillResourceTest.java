package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
class SkillResourceTest {

        @Test
        void skillListSupportsPagination() {
                for (int index = 0; index < 6; index++) {
                        String unique = UUID.randomUUID().toString().replaceAll("[^A-Za-z]", "");
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("name", "Pagination Skill " + unique);
                        payload.put("category", "Backend");

                        given()
                                        .contentType(ContentType.JSON)
                                        .body(payload)
                                        .when()
                                        .post("/api/skills")
                                        .then()
                                        .statusCode(201);
                }

                given()
                                .when()
                                .get("/api/skills")
                                .then()
                                .statusCode(200)
                                .body("data", hasSize(5))
                                .body("totalRecords", greaterThanOrEqualTo(6))
                                .body("page", equalTo(0))
                                .body("pageSize", equalTo(5))
                                .body("nextPage", equalTo(1))
                                .body("previousPage", nullValue());

                String firstPageId = given()
                                .queryParam("page", 0)
                                .queryParam("pageSize", 1)
                                .when()
                                .get("/api/skills")
                                .then()
                                .statusCode(200)
                                .body("data", hasSize(1))
                                .extract()
                                .path("data[0].id");

                given()
                                .queryParam("page", 1)
                                .queryParam("pageSize", 1)
                                .when()
                                .get("/api/skills")
                                .then()
                                .statusCode(200)
                                .body("data", hasSize(1))
                                .body("totalRecords", greaterThanOrEqualTo(6))
                                .body("nextPage", notNullValue())
                                .body("previousPage", equalTo(0))
                                .body("data[0].id", notNullValue())
                                .body("data[0].id", not(equalTo(firstPageId)));
        }

        @Test
        void skillCrudFlow() {
                String unique = String.valueOf(System.nanoTime());
                String skillName = "Java " + unique.replaceAll("[^A-Za-z]", "");
                if (skillName.length() > 100) {
                        skillName = "Java Architecture";
                }

                Map<String, Object> payload = new HashMap<>();
                payload.put("name", skillName);
                payload.put("category", "Backend");

                String skillId = given()
                                .contentType(ContentType.JSON)
                                .body(payload)
                                .when()
                                .post("/api/skills")
                                .then()
                                .statusCode(201)
                                .body("id", notNullValue())
                                .extract()
                                .path("id");

                given()
                                .when()
                                .get("/api/skills/{id}", skillId)
                                .then()
                                .statusCode(200)
                                .body("id", equalTo(skillId));

                Map<String, Object> update = new HashMap<>(payload);
                update.put("category", "Full Stack");

                given()
                                .contentType(ContentType.JSON)
                                .body(update)
                                .when()
                                .put("/api/skills/{id}", skillId)
                                .then()
                                .statusCode(200)
                                .body("category", equalTo("Full Stack"));

                given()
                                .when()
                                .delete("/api/skills/{id}", skillId)
                                .then()
                                .statusCode(204);
        }
}
