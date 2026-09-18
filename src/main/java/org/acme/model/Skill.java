package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "tb_skill", schema = "talent")
public class Skill extends PanacheEntityBase {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Skill name cannot be blank.")
    @Size(min = 2, max = 100, message = "Skill name must be between 2 and 100 characters.")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters.")
    public String name;

    @Column(nullable = false, length = 50)
    public String category;
}