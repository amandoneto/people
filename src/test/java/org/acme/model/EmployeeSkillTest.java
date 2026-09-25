package org.acme.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

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

}
