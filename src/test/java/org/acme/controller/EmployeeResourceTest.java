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
class EmployeeResourceTest {

    @Test
    void employeeCrudFlow() {
        String unique = String.valueOf(System.nanoTime());
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Alice " + unique);
        payload.put("email", "alice." + unique + "@example.com");
        payload.put("role", "Developer");
        payload.put("seniority", "Junior");

        String employeeId = given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/employees")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/employees/{id}", employeeId)
                .then()
                .statusCode(200)
                .body("id", equalTo(employeeId));

        Map<String, Object> update = new HashMap<>(payload);
        update.put("role", "Senior Developer");
        update.put("seniority", "Senior");

        given()
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .put("/api/employees/{id}", employeeId)
                .then()
                .statusCode(200)
                .body("role", equalTo("Senior Developer"))
                .body("seniority", equalTo("Senior"));

        given()
                .when()
                .delete("/api/employees/{id}", employeeId)
                .then()
                .statusCode(204);
    }
}
