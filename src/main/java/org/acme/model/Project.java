package org.acme.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/** Project entity persisted in the talent schema. */
@Entity
@Table(name = "tb_project", schema = "talent")
public class Project {

    /** Creates an empty project entity. */
    public Project() {
    }

    /** Stable project identifier. */
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    /** Project name. */
    @Column(nullable = false, length = 150)
    public String name;

    /** Project description, which may be {@code null}. */
    @Column(columnDefinition = "TEXT")
    public String description;

    // PLANNING, ACTIVE, COMPLETED, CANCELLED
    /** Project lifecycle status. */
    @Column(nullable = false, length = 30)
    public String status;

    /** Local date and time when the entity was initialized. */
    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    /** Local date and time when the project was last updated. */
    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();
}
