package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_employee", schema = "talent")
public class Employee extends PanacheEntityBase {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME) // Gera o UUIDv7 automaticamente
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false, length = 150)
    public String name;

    @Column(nullable = false, unique = true, length = 150)
    public String email;

    @Column(nullable = false, length = 100)
    public String role;

    @Column(nullable = false, length = 50)
    public String seniority;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    @Column(name = "password", nullable = false, length = 255)
    public String password;

    @Column(name = "created_at", updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    public LocalDateTime updatedAt = LocalDateTime.now();
}
