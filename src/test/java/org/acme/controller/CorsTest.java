package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class CorsTest {

    @Test
    void allowsAngularOriginToSendAuthenticatedPostRequests() {
        given()
                .header("Origin", "http://localhost:4200")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "authorization,content-type")
                .when()
                .options("/api/employees")
                .then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", equalTo("http://localhost:4200"))
                .header("Access-Control-Allow-Methods", containsString("POST"))
                .header("Access-Control-Allow-Headers", containsStringIgnoringCase("authorization"))
                .header("Access-Control-Allow-Headers", containsStringIgnoringCase("content-type"))
                .header("Access-Control-Allow-Credentials", equalTo("false"));
    }
}
