package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "tb_skill")
public class Skill extends PanacheEntityBase {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false, unique = true, length = 100)
    public String name;

    @Column(nullable = false, length = 50)
    public String category;
}