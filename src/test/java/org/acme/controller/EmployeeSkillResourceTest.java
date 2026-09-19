package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
class EmployeeSkillResourceTest {

    @Test
    void employeeSkillCrudFlow() {
        String employeeId = createEmployee();
        String skillId = createSkill();

        Map<String, Object> payload = new HashMap<>();
        payload.put("employee", Map.of("id", employeeId));
        payload.put("skill", Map.of("id", skillId));
        payload.put("proficiencyLevel", "Advanced");

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/employee-skills")
                .then()
                .statusCode(201);

        given()
                .when()
                .get("/api/employee-skills")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));

        given()
                .when()
                .delete("/api/employee-skills/{employeeId}/{skillId}", employeeId, skillId)
                .then()
                .statusCode(204);

        given().when().delete("/api/employees/{id}", employeeId).then().statusCode(204);
        given().when().delete("/api/skills/{id}", skillId).then().statusCode(204);
    }

    private String createEmployee() {
        String unique = String.valueOf(System.nanoTime());
        Map<String, Object> employee = new HashMap<>();
        employee.put("name", "Employee Skill Owner " + unique);
        employee.put("email", "employee.skill." + unique + "@example.com");
        employee.put("role", "Engineer");
        employee.put("seniority", "Senior");

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

    private String createSkill() {
        String unique = String.valueOf(System.nanoTime());
        String skillName = "Skill " + unique.replaceAll("[^A-Za-z]", "");
        if (skillName.length() > 100) {
            skillName = "Skill Architecture";
        }

        Map<String, Object> skill = new HashMap<>();
        skill.put("name", skillName);
        skill.put("category", "Backend");

        return given()
                .contentType(ContentType.JSON)
                .body(skill)
                .when()
                .post("/api/skills")
                .then()
                .statusCode(201)
                .extract()
                .path("id");
    }
}
