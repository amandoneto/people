package org.acme.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EmployeeUpdateRequest(
        String name,
        String email,
        String role,
        String seniority) {
}