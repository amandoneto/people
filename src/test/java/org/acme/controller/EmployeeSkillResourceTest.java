package org.acme.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItem;

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

    @Test
    void findsEmployeesGroupedBySkillName() {
        String employeeA = "Java Employee " + uniqueLetters();
        String employeeB = "Gcp Employee " + uniqueLetters();
        String skillA = "Java" + uniqueLetters();
        String skillB = "Gcp" + uniqueLetters();

        String employeeAId = createEmployee(employeeA);
        String employeeBId = createEmployee(employeeB);
        String skillAId = createSkill(skillA);
        String skillBId = createSkill(skillB);

        createEmployeeSkill(employeeAId, skillAId);
        createEmployeeSkill(employeeBId, skillBId);

        given()
                .contentType(ContentType.JSON)
                .body(java.util.List.of(skillA, skillB))
                .when()
                .post("/api/employee-skills/search")
                .then()
                .statusCode(200)
                .body(skillA, contains(employeeA))
                .body(skillB, contains(employeeB))
                .body(skillA, not(hasItem(employeeB)))
                .body(skillB, not(hasItem(employeeA)));
    }

    @Test
    void rejectsAssociationWhenEmployeeOrSkillDoesNotExist() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("employee", Map.of("id", java.util.UUID.randomUUID().toString()));
        payload.put("skill", Map.of("id", java.util.UUID.randomUUID().toString()));
        payload.put("proficiencyLevel", "Advanced");

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/employee-skills")
                .then()
                .statusCode(400);
    }

    private String createEmployee() {
        return createEmployee("Employee Skill Owner " + uniqueLetters());
    }

    private String createEmployee(String name) {
        Map<String, Object> employee = new HashMap<>();
        employee.put("name", name);
        employee.put("email", "employee.skill." + uniqueLetters() + "@example.com");
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
        return createSkill("Skill" + uniqueLetters());
    }

    private String createSkill(String skillName) {

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

    private void createEmployeeSkill(String employeeId, String skillId) {
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
    }

    private String uniqueLetters() {
        return java.util.UUID.randomUUID().toString().replaceAll("[^a-f]", "");
    }
}
