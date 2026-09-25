package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import org.acme.model.Employee;
import org.acme.service.AuthenticationService;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;

@QuarkusTest
class RealJwtAuthenticationTest {

    @Inject
    UserTransaction userTransaction;

    @Test
    void issuedTokenAuthenticatesAgainstProtectedResource() {
        String unique = String.valueOf(System.nanoTime());
        Employee employee = new Employee();
        employee.name = "Real JWT User";
        employee.email = "real.jwt." + unique + "@example.com";
        employee.role = "admin";
        employee.seniority = "Senior";
        employee.password = AuthenticationService.hashPassword("TestPassword123!");
        persist(employee);

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

        delete(employee.email);
    }

    private void persist(Employee employee) {
        try {
            userTransaction.begin();
            employee.persistAndFlush();
            userTransaction.commit();
        } catch (Exception exception) {
            rollback();
            throw new IllegalStateException("Unable to persist test employee", exception);
        }
    }

    private void delete(String email) {
        try {
            userTransaction.begin();
            Employee.delete("email", email);
            userTransaction.commit();
        } catch (Exception exception) {
            rollback();
            throw new IllegalStateException("Unable to delete test employee", exception);
        }
    }

    private void rollback() {
        try {
            if (userTransaction.getStatus() != jakarta.transaction.Status.STATUS_NO_TRANSACTION) {
                userTransaction.rollback();
            }
        } catch (Exception ignored) {
            // Preserve the original test failure.
        }
    }
}