package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "tb_project")
public class Project extends PanacheEntityBase {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID id;

    @Column(nullable = false, length = 150)
    public String name;

    @Column(columnDefinition = "TEXT")
    public String description;

    @Column(nullable = false, length = 30)
    public String status;
}
