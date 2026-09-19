package org.acme.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class AllocationTest {

    @Test
    void shouldHoldEmployeeProjectAllocationData() {
        Employee employee = new Employee();
        employee.name = "Carlos Silva";
        employee.email = "carlos.silva@example.com";
        employee.role = "Architect";
        employee.seniority = "Lead";

        Project project = new Project();
        project.name = "Core Platform";
        project.status = "ACTIVE";

        Allocation allocation = new Allocation();
        allocation.employee = employee;
        allocation.project = project;
        allocation.allocationPercentage = 80;
        allocation.startDate = LocalDate.of(2025, 1, 1);
        allocation.endDate = LocalDate.of(2025, 12, 31);

        assertEquals(employee, allocation.employee);
        assertEquals(project, allocation.project);
        assertEquals(80, allocation.allocationPercentage);
        assertEquals(LocalDate.of(2025, 1, 1), allocation.startDate);
        assertEquals(LocalDate.of(2025, 12, 31), allocation.endDate);
    }
}
