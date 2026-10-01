package org.acme.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record EmployeeEventPayload(
        UUID employeeId,
        String name,
        String email,
        String role,
        String seniority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Instant deletedAt) {
}