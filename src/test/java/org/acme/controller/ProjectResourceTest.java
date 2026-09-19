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
class ProjectResourceTest {

    @Test
    void projectCrudFlow() {
        String unique = String.valueOf(System.nanoTime());
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Project " + unique);
        payload.put("description", "Quarkus project for integration testing");
        payload.put("status", "PLANNING");

        String projectId = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/projects")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/projects/{id}", projectId)
                .then()
                .statusCode(200)
                .body("id", equalTo(projectId));

        Map<String, Object> update = new HashMap<>(payload);
        update.put("status", "ACTIVE");
        update.put("description", "Updated project description");

        given()
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .put("/api/projects/{id}", projectId)
                .then()
                .statusCode(200)
                .body("status", equalTo("ACTIVE"))
                .body("description", equalTo("Updated project description"));

        given()
                .when()
                .delete("/api/projects/{id}", projectId)
                .then()
                .statusCode(204);
    }
}
