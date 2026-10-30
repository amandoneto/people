package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.Project;
import org.acme.repository.ProjectRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Business operations for project persistence and message-driven upserts. */
@ApplicationScoped
public class ProjectService {

    /** Creates the project service. */
    public ProjectService() {
    }

    @Inject
    ProjectRepository repository;

    /**
     * Lists all persisted projects.
     *
     * @return all projects
     */
    public List<Project> listAll() {
        return repository.listAll();
    }

    /**
     * Finds a project by identifier.
     *
     * @param id project identifier
     * @return the project, or {@code null} if none exists
     */
    public Project getById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Creates a project with a newly assigned identifier.
     *
     * @param project project to persist
     * @return the created project
     */
    @Transactional
    public Project create(Project project) {
        project.id = UUID.randomUUID();
        repository.persist(project);
        return project;
    }

    /**
     * Updates an existing project with a supplied identifier or creates a new project.
     *
     * @param project project values to persist
     * @return the updated or created project
     */
    @Transactional
    public Project createOrUpdate(Project project) {
        if (project.id != null) {
            Project existingProject = repository.findById(project.id);
            if (existingProject != null) {
                existingProject.name = project.name;
                existingProject.description = project.description;
                existingProject.status = project.status;
                existingProject.updatedAt = LocalDateTime.now();
                return existingProject;
            }
        }

        project.id = UUID.randomUUID();
        repository.persist(project);
        return project;
    }

    /**
     * Inserts or updates a project while retaining the message's stable identifier.
     *
     * @param project project received from a message
     * @return the persisted or updated project
     * @throws IllegalArgumentException if the project has no identifier
     */
    @Transactional
    public Project upsertFromMessage(Project project) {
        if (project.id == null) {
            throw new IllegalArgumentException("A project message must include a stable project ID.");
        }

        Project existingProject = repository.findById(project.id);
        if (existingProject != null) {
            boolean changed = !existingProject.name.equals(project.name)
                    || !Objects.equals(existingProject.description, project.description)
                    || !existingProject.status.equals(project.status);
            if (changed) {
                existingProject.name = project.name;
                existingProject.description = project.description;
                existingProject.status = project.status;
                existingProject.updatedAt = LocalDateTime.now();
            }
            return existingProject;
        }

        repository.persist(project);
        return project;
    }

    /**
     * Replaces the mutable values of an existing project.
     *
     * @param id project identifier
     * @param updatedProject replacement project values
     * @return the updated project, or {@code null} if it does not exist
     */
    @Transactional
    public Project update(UUID id, Project updatedProject) {
        Project project = repository.findById(id);
        if (project == null) {
            return null;
        }

        project.name = updatedProject.name;
        project.description = updatedProject.description;
        project.status = updatedProject.status;
        project.updatedAt = LocalDateTime.now();
        return project;
    }

    /**
     * Deletes a project by identifier.
     *
     * @param id project identifier
     * @return {@code true} if a project was deleted
     */
    @Transactional
    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
