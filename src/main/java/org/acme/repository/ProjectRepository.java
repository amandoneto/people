package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Project;

import java.util.UUID;

/** Panache persistence repository for projects keyed by UUID. */
@ApplicationScoped
public class ProjectRepository implements PanacheRepositoryBase<Project, UUID> {

    /** Creates the project repository. */
    public ProjectRepository() {
    }
}
