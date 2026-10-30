package org.acme.controller;

import io.quarkus.security.Authenticated;
import org.acme.model.Project;
import org.acme.service.ProjectService;
import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

/** REST endpoints for managing projects. */
@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectResource {

    // Criação do logger padrão da classe
    private static final Logger LOG = Logger.getLogger(ProjectResource.class);

    /** Creates the project resource. */
    public ProjectResource() {
    }

    @Inject
    ProjectService projectService;

    /**
     * Lists all projects.
     *
     * @return all persisted projects
     */
    @GET
    @RolesAllowed({ "admin", "user" })
    public List<Project> listAll() {
        LOG.info("Searching all the projects...");
        return projectService.listAll();
    }

    /**
     * Finds a project by its identifier.
     *
     * @param id project identifier
     * @return the project, or a not-found response
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response getById(@PathParam("id") UUID id) {
        LOG.infov("Searching for project id {0}.", id);
        Project project = projectService.getById(id);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(project).build();
    }

    /**
     * Creates a project.
     *
     * @param project project data
     * @return a created response containing the project
     */
    @POST
    @RolesAllowed("admin")
    public Response create(Project project) {
        LOG.info("Creating a project.");
        Project created = projectService.create(project);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Updates a project.
     *
     * @param id project identifier
     * @param updatedProject replacement project data
     * @return the updated project, or a not-found response
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response update(@PathParam("id") UUID id, Project updatedProject) {
        LOG.infov("Updating project id {0}.", id);

        Project project = projectService.update(id, updatedProject);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(project).build();
    }

    /**
     * Deletes a project.
     *
     * @param id project identifier
     * @return a no-content response, or a not-found response
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") UUID id) {
        LOG.infov("Deleting project id {0}.", id);
        boolean deleted = projectService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}