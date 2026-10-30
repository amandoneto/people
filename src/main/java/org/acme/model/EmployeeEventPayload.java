package org.acme.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Employee state captured in an event payload.
 *
 * @param employeeId employee identifier
 * @param name employee name
 * @param email employee email address
 * @param role employee role
 * @param seniority employee seniority level
 * @param createdAt employee creation time
 * @param updatedAt most recent profile update time represented by this state
 * @param deletedAt deletion time, or {@code null} while the employee is active
 */
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