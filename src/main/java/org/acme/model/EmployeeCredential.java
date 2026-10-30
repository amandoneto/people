package org.acme.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

/** Stored password hash and update time associated with an employee. */
@Entity
@Table(name = "tb_employee_credential", schema = "talent")
public class EmployeeCredential {

    /** Creates an empty employee credential entity. */
    public EmployeeCredential() {
    }

    /** Identifier of the employee whose credential is stored. */
    @Id
    @Column(name = "employee_id", nullable = false, updatable = false)
    public UUID employeeId;

    /** Hashed password; plaintext passwords are not stored in this entity. */
    @Column(name = "password_hash", nullable = false, length = 255)
    public String passwordHash;

    /** Timestamp maintained when the credential entity is updated. */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}