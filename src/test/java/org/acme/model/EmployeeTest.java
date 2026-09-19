package org.acme.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EmployeeTest {

    @Test
    void shouldCreateEmployeeWithDefaultsAndValues() {
        Employee employee = new Employee();
        employee.name = "Ana Souza";
        employee.email = "ana.souza@example.com";
        employee.role = "Developer";
        employee.seniority = "Senior";

        assertNull(employee.id);
        assertEquals("Ana Souza", employee.name);
        assertEquals("ana.souza@example.com", employee.email);
        assertEquals("Developer", employee.role);
        assertEquals("Senior", employee.seniority);
        assertNotNull(employee.createdAt);
        assertNotNull(employee.updatedAt);
        assertTrue(employee.createdAt instanceof LocalDateTime);
        assertTrue(employee.updatedAt instanceof LocalDateTime);
    }
}
