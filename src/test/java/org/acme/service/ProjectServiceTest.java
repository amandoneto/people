package org.acme.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.Project;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ProjectServiceTest {

    @Inject
    ProjectService service;

    @Test
    @Transactional
    void createsProjectWithoutIdAndUpdatesProjectWithExistingId() {
        Project newProject = new Project();
        newProject.name = "Initial Project";
        newProject.description = "Initial description";
        newProject.status = "PLANNING";

        Project createdProject = service.createOrUpdate(newProject);
        assertNotNull(createdProject.id);

        Project update = new Project();
        update.id = createdProject.id;
        update.name = "Updated Project";
        update.description = "Updated description";
        update.status = "ACTIVE";

        Project updatedProject = service.createOrUpdate(update);

        assertEquals(createdProject.id, updatedProject.id);
        assertEquals("Updated Project", updatedProject.name);
        assertEquals("Updated description", updatedProject.description);
        assertEquals("ACTIVE", updatedProject.status);
    }

    @Test
    @Transactional
    void createsProjectWithGeneratedIdWhenProvidedIdDoesNotExist() {
        Project project = new Project();
        UUID suppliedId = UUID.randomUUID();
        project.id = suppliedId;
        project.name = "New Project";
        project.description = "Created when the supplied ID was not found.";
        project.status = "PLANNING";

        Project createdProject = service.createOrUpdate(project);

        assertNotNull(createdProject.id);
        assertNotEquals(suppliedId, createdProject.id);
    }
}
