package org.acme.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_employee_credential", schema = "talent")
public class EmployeeCredential {

    @Id
    @Column(name = "employee_id", nullable = false, updatable = false)
    public UUID employeeId;

    @Column(name = "password_hash", nullable = false, length = 255)
    public String passwordHash;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    public Instant updatedAt;
}