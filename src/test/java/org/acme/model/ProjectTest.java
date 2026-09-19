package org.acme.model;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProjectTest {

    @Test
    void shouldCreateProjectWithDefaultsAndValues() {
        Project project = new Project();
        project.name = "People Platform";
        project.description = "Employee and skill management application";
        project.status = "ACTIVE";

        assertNull(project.id);
        assertEquals("People Platform", project.name);
        assertEquals("Employee and skill management application", project.description);
        assertEquals("ACTIVE", project.status);
        assertNotNull(project.createdAt);
        assertNotNull(project.updatedAt);
        assertTrue(project.createdAt instanceof LocalDateTime);
        assertTrue(project.updatedAt instanceof LocalDateTime);
    }
}
