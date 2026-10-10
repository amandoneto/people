package org.acme.dto;

import java.util.UUID;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.acme.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectDTOTest {

    @Test
    void validatesProjectFields() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            ProjectDTO validProject = new ProjectDTO(
                    null,
                    "Projeto Ágil",
                    "Entrega v2.0: código, testes & revisão (2026).",
                    "PLANNING");
            assertTrue(validator.validate(validProject).isEmpty());

            assertTrue(validator.validate(new ProjectDTO(null, "Projeto", null, "PLANNING")).isEmpty());
            assertTrue(validator.validate(new ProjectDTO(null, "Projeto", null, "ACTIVE")).isEmpty());
            assertTrue(validator.validate(new ProjectDTO(null, "Projeto", null, "COMPLETED")).isEmpty());
            assertTrue(validator.validate(new ProjectDTO(null, "Projeto", null, "CANCELLED")).isEmpty());

            assertFalse(validator.validate(new ProjectDTO(null, "Projeto 2", null, "PLANNING")).isEmpty());
            assertFalse(validator.validate(new ProjectDTO(null, "Projeto", "texto $não permitido", "PLANNING")).isEmpty());
            assertFalse(validator.validate(new ProjectDTO(null, "Projeto", null, "ON_HOLD")).isEmpty());
            assertFalse(validator.validate(new ProjectDTO(null, " ", null, "PLANNING")).isEmpty());
        }
    }

    @Test
    void mapsDtoFieldsToProject() {
        UUID id = UUID.randomUUID();
        ProjectDTO dto = new ProjectDTO(id, "Projeto Ágil", "Descrição 2026!", "ACTIVE");

        Project project = dto.toProject();

        assertEquals(id, project.id);
        assertEquals(dto.name(), project.name);
        assertEquals(dto.description(), project.description);
        assertEquals(dto.status(), project.status);
    }
}
