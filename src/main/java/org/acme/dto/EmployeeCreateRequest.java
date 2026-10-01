package org.acme.dto;

import jakarta.validation.constraints.NotBlank;

public record EmployeeCreateRequest(
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String role,
        @NotBlank String seniority,
        @NotBlank String password) {
}