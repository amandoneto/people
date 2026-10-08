package org.acme.repository;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.Employee;
import org.acme.model.EmployeeSkill;
import org.acme.model.EmployeeSkillId;
import org.acme.model.Skill;
import org.acme.dto.EmployeeCreateRequest;
import org.acme.service.EmployeeService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests persistence operations for employee-skill associations.
 */
@QuarkusTest
class EmployeeSkillRepositoryTest {

    @Inject
    EmployeeSkillRepository repository;

    @Inject
    EmployeeService employeeService;

    @Inject
    SkillRepository skillRepository;

    @Test
    @Transactional
    void shouldFindEmployeeSkillsBySkillNames() {
        String suffix = UUID.randomUUID().toString().replaceAll("[^a-f]", "");
        Employee employee = createEmployee(suffix);
        Skill skill = createSkill(suffix);
        EmployeeSkill employeeSkill = new EmployeeSkill();
        employeeSkill.id = new EmployeeSkillId(employee.id, skill.id);
        employeeSkill.employee = employee;
        employeeSkill.skill = skill;
        employeeSkill.proficiencyLevel = "Advanced";
        repository.persist(employeeSkill);

        List<EmployeeSkill> matches = repository.findBySkillNames(List.of(skill.name));

        assertEquals(1, matches.size());
        assertEquals(employee.name, matches.get(0).employee.name);
        assertEquals(skill.name, matches.get(0).skill.name);
    }

    @Test
    void shouldReturnNoMatchesForMissingSkillNames() {
        assertTrue(repository.findBySkillNames(null).isEmpty());
        assertTrue(repository.findBySkillNames(List.of()).isEmpty());
    }

    private Employee createEmployee(String suffix) {
        return employeeService.create(new EmployeeCreateRequest(
                "Repository Employee " + suffix,
                "repository." + suffix + "@example.com",
                "Developer",
                "Senior",
                "TestPassword123!"));
    }

    private Skill createSkill(String suffix) {
        Skill skill = new Skill();
        skill.name = "Repository Skill " + suffix;
        skill.category = "Backend";
        skillRepository.persist(skill);
        return skill;
    }
}