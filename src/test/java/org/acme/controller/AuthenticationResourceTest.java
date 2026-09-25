package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestSecurity(user = "test-admin", roles = "admin")
class AuthenticationResourceTest {

    @Test
    void loginReturnsTokenWithIdentityClaims() {
        String unique = String.valueOf(System.nanoTime());
        String email = "auth." + unique + "@example.com";
        Map<String, Object> employee = new HashMap<>();
        employee.put("name", "Authentication User");
        employee.put("email", email);
        employee.put("role", "admin");
        employee.put("seniority", "Senior");
        employee.put("password", "TestPassword123!");

        given()
                .contentType(ContentType.JSON)
                .body(employee)
                .post("/api/employees")
                .then()
                .statusCode(201);

        String token = given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", "TestPassword123!"))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token");

        String[] tokenParts = token.split("\\.");
        String claims = new String(java.util.Base64.getUrlDecoder().decode(tokenParts[1]),
                java.nio.charset.StandardCharsets.UTF_8);
        org.junit.jupiter.api.Assertions.assertTrue(claims.contains("\"exp\":"));

        org.junit.jupiter.api.Assertions.assertEquals(3, tokenParts.length);
    }

    @Test
    void loginRejectsInvalidPassword() {
        given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "missing@example.com", "password", "wrong"))
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "test-user", roles = "user")
    void userCanReadButCannotCreate() {
        given()
                .when()
                .get("/api/projects")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "Unauthorized project", "status", "PLANNING"))
                .when()
                .post("/api/projects")
                .then()
                .statusCode(403);
    }
}