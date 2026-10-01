package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.acme.dto.EmployeeCreateRequest;
import org.acme.model.Employee;
import org.acme.service.EmployeeService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
class RealJwtAuthenticationTest {

    @Inject
    EmployeeService employeeService;

    @Test
    void issuedTokenAuthenticatesAgainstProtectedResource() {
        String unique = String.valueOf(System.nanoTime());
        Employee employee = employeeService.create(new EmployeeCreateRequest(
                "Real JWT User",
                "real.jwt." + unique + "@example.com",
                "admin",
                "Senior",
                "TestPassword123!"));

        String token = given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", employee.email, "password", "TestPassword123!"))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        given()
                .auth().oauth2(token)
                .when()
                .get("/api/employees")
                .then()
                .statusCode(200)
                .body("data", org.hamcrest.Matchers.notNullValue());

        employeeService.delete(employee.id);
    }
}