package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@QuarkusTest
@TestSecurity(user = "test-admin", roles = "admin")
class EmployeeResourceTest {

        @Test
        void employeeListSupportsPagination() {
                for (int index = 0; index < 6; index++) {
                        String unique = String.valueOf(System.nanoTime()) + index;
                        Map<String, Object> payload = new HashMap<>();
                        payload.put("name", "Pagination Employee " + unique);
                        payload.put("email", "pagination.employee." + unique + "@example.com");
                        payload.put("role", "Developer");
                        payload.put("seniority", "Junior");
                        payload.put("password", "TestPassword123!");

                        given()
                                        .contentType(ContentType.JSON)
                                        .body(payload)
                                        .when()
                                        .post("/api/employees")
                                        .then()
                                        .statusCode(201);
                }

                given()
                                .when()
                                .get("/api/employees")
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
                                .get("/api/employees")
                                .then()
                                .statusCode(200)
                                .body("data", hasSize(1))
                                .extract()
                                .path("data[0].id");

                given()
                                .queryParam("page", 1)
                                .queryParam("pageSize", 1)
                                .when()
                                .get("/api/employees")
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
        void employeeCrudFlow() {
                String unique = String.valueOf(System.nanoTime());
                Map<String, Object> payload = new HashMap<>();
                payload.put("name", "Alice " + unique);
                payload.put("email", "alice." + unique + "@example.com");
                payload.put("role", "Developer");
                payload.put("seniority", "Junior");
                payload.put("password", "TestPassword123!");

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
