package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestSecurity(user = "test-admin", roles = "admin")
class AllocationResourceTest {

        @Test
        void allocationCrudFlow() {
                String employeeId = createEmployee();
                String projectId = createProject();

                Map<String, Object> payload = new HashMap<>();
                payload.put("employee", Map.of("id", employeeId));
                payload.put("project", Map.of("id", projectId));
                payload.put("allocationPercentage", 75);
                payload.put("startDate", "2025-01-01");
                payload.put("endDate", "2025-12-31");

                String allocationId = given()
                                .contentType(ContentType.JSON)
                                .body(payload)
                                .when()
                                .post("/api/allocations")
                                .then()
                                .statusCode(201)
                                .body("id", notNullValue())
                                .extract()
                                .path("id");

                given()
                                .when()
                                .get("/api/allocations/{id}", allocationId)
                                .then()
                                .statusCode(200)
                                .body("id", equalTo(allocationId));

                Map<String, Object> update = new HashMap<>(payload);
                update.put("allocationPercentage", 90);
                update.put("endDate", "2025-10-31");

                given()
                                .contentType(ContentType.JSON)
                                .body(update)
                                .when()
                                .put("/api/allocations/{id}", allocationId)
                                .then()
                                .statusCode(200)
                                .body("allocationPercentage", equalTo(90));

                given()
                                .when()
                                .delete("/api/allocations/{id}", allocationId)
                                .then()
                                .statusCode(204);

                given().when().delete("/api/employees/{id}", employeeId).then().statusCode(204);
                given().when().delete("/api/projects/{id}", projectId).then().statusCode(204);
        }

        private String createEmployee() {
                String unique = String.valueOf(System.nanoTime());
                Map<String, Object> employee = new HashMap<>();
                employee.put("name", "Employee " + unique);
                employee.put("email", "employee." + unique + "@example.com");
                employee.put("role", "Developer");
                employee.put("seniority", "Mid");
                employee.put("password", "TestPassword123!");

                return given()
                                .contentType(ContentType.JSON)
                                .body(employee)
                                .when()
                                .post("/api/employees")
                                .then()
                                .statusCode(201)
                                .extract()
                                .path("id");
        }

        private String createProject() {
                String unique = String.valueOf(System.nanoTime());
                Map<String, Object> project = new HashMap<>();
                project.put("name", "Allocation Project " + unique);
                project.put("description", "Project for allocation test");
                project.put("status", "ACTIVE");

                return given()
                                .contentType(ContentType.JSON)
                                .body(project)
                                .when()
                                .post("/api/projects")
                                .then()
                                .statusCode(201)
                                .extract()
                                .path("id");
        }
}
