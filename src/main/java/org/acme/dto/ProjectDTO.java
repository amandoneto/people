package org.acme.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.acme.model.Project;

public record ProjectDTO(
        UUID id,
        @NotBlank(message = "Project name cannot be blank.")
        @Size(max = 150, message = "Project name cannot exceed 150 characters.")
        @Pattern(regexp = "^[\\p{L} ]+$", message = "Project name must contain only letters and spaces.")
        String name,
        @Pattern(regexp = "^[\\p{L}\\p{N}\\p{P}\\p{Zs}\\r\\n\\t]*$", message = "Project description can contain only letters, digits, punctuation, and spaces.")
        String description,
        @NotBlank(message = "Project status cannot be blank.")
        @Pattern(regexp = "^(PLANNING|ACTIVE|COMPLETED|CANCELLED)$", message = "Project status must be PLANNING, ACTIVE, COMPLETED, or CANCELLED.")
        String status) {

    public Project toProject() {
        Project project = new Project();
        project.id = id;
        project.name = name;
        project.description = description;
        project.status = status;
        return project;
    }
}
