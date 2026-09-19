package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class SkillResourceTest {

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
