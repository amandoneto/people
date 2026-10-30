package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Profile values used to update an employee.
 *
 * @param name employee name
 * @param email employee email address
 * @param role employee role
 * @param seniority employee seniority level
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeeUpdateRequest(
        String name,
        String email,
        String role,
        String seniority) {
}