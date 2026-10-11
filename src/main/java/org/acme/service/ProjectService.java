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

@ApplicationScoped
public class ProjectService {

    @Inject
    ProjectRepository repository;

    public List<Project> listAll() {
        return repository.listAll();
    }

    public Project getById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public Project create(Project project) {
        project.id = UUID.randomUUID();
        repository.persist(project);
        return project;
    }

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

    @Transactional
    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
