package org.acme.model;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EmployeeSkillTest {

    @Test
    void shouldCreateCompositeKeyAndAssociateEmployeeAndSkill() {
        Employee employee = new Employee();
        employee.name = "Bruna Costa";
        employee.email = "bruna.costa@example.com";
        employee.role = "QA";
        employee.seniority = "Mid";

        Skill skill = new Skill();
        skill.name = "Testing";
        skill.category = "Quality";

        UUID employeeId = UUID.randomUUID();
        UUID skillId = UUID.randomUUID();

        EmployeeSkillId id = new EmployeeSkillId(employeeId, skillId);
        EmployeeSkill employeeSkill = new EmployeeSkill();
        employeeSkill.id = id;
        employeeSkill.employee = employee;
        employeeSkill.skill = skill;
        employeeSkill.proficiencyLevel = "Advanced";

        assertEquals(employeeId, employeeSkill.id.employeeId);
        assertEquals(skillId, employeeSkill.id.skillId);
        assertEquals(employee, employeeSkill.employee);
        assertEquals(skill, employeeSkill.skill);
        assertEquals("Advanced", employeeSkill.proficiencyLevel);
    }

    @Test
    @Transactional
    void shouldFindEmployeeSkillsBySkillNames() {
        String suffix = UUID.randomUUID().toString().replaceAll("[^a-f]", "");

        Employee javaEmployee = new Employee();
        javaEmployee.name = "Java Query Employee " + suffix;
        javaEmployee.email = "java.query." + suffix + "@example.com";
        javaEmployee.role = "Developer";
        javaEmployee.seniority = "Senior";
        javaEmployee.persist();

        Employee gcpEmployee = new Employee();
        gcpEmployee.name = "Gcp Query Employee " + suffix;
        gcpEmployee.email = "gcp.query." + suffix + "@example.com";
        gcpEmployee.role = "Developer";
        gcpEmployee.seniority = "Senior";
        gcpEmployee.persist();

        Skill javaSkill = new Skill();
        javaSkill.name = "Java Query Skill " + suffix;
        javaSkill.category = "Backend";
        javaSkill.persist();

        Skill gcpSkill = new Skill();
        gcpSkill.name = "Gcp Query Skill " + suffix;
        gcpSkill.category = "Cloud";
        gcpSkill.persist();

        EmployeeSkill javaEmployeeSkill = new EmployeeSkill();
        javaEmployeeSkill.id = new EmployeeSkillId(javaEmployee.id, javaSkill.id);
        javaEmployeeSkill.employee = javaEmployee;
        javaEmployeeSkill.skill = javaSkill;
        javaEmployeeSkill.proficiencyLevel = "Advanced";
        javaEmployeeSkill.persist();

        EmployeeSkill gcpEmployeeSkill = new EmployeeSkill();
        gcpEmployeeSkill.id = new EmployeeSkillId(gcpEmployee.id, gcpSkill.id);
        gcpEmployeeSkill.employee = gcpEmployee;
        gcpEmployeeSkill.skill = gcpSkill;
        gcpEmployeeSkill.proficiencyLevel = "Intermediate";
        gcpEmployeeSkill.persist();

        List<EmployeeSkill> matches = EmployeeSkill.findBySkillNames(List.of(javaSkill.name));

        assertEquals(1, matches.size());
        assertEquals(javaEmployee.name, matches.get(0).employee.name);
        assertEquals(javaSkill.name, matches.get(0).skill.name);
    }

    @Test
    void shouldReturnNoMatchesForMissingSkillNames() {
        assertTrue(EmployeeSkill.findBySkillNames(null).isEmpty());
        assertTrue(EmployeeSkill.findBySkillNames(List.of()).isEmpty());
    }
}
