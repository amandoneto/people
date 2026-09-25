package org.acme.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.EmployeeSkill;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests business operations for employee-skill associations.
 */
@QuarkusTest
class EmployeeSkillServiceTest {

    @Inject
    EmployeeSkillService service;

    @Test
    void shouldReturnEmptyGroupsForNullSkillNames() {
        assertTrue(service.findEmployeesBySkills(null).isEmpty());
    }

    @Test
    void shouldReturnRequestedSkillNamesAsEmptyGroupsWhenNoAssociationExists() {
        Map<String, List<String>> result = service.findEmployeesBySkills(List.of("Missing Skill"));

        assertEquals(List.of(), result.get("Missing Skill"));
    }

    @Test
    void shouldReturnAllEmployeeSkills() {
        List<EmployeeSkill> result = service.listAll();

        assertTrue(result != null);
    }
}