package org.acme.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Required profile and credential data for creating an employee.
 *
 * @param name employee name
 * @param email employee email address
 * @param role employee role
 * @param seniority employee seniority level
 * @param password plaintext password supplied for credential hashing
 */
public record EmployeeCreateRequest(
        @NotBlank String name,
        @NotBlank String email,
        @NotBlank String role,
        @NotBlank String seniority,
        @NotBlank String password) {
}