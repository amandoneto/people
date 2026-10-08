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

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectResource {

    // Criação do logger padrão da classe
    private static final Logger LOG = Logger.getLogger(ProjectResource.class);

    @Inject
    ProjectService projectService;

    @GET
    @RolesAllowed({ "admin", "user" })
    public List<Project> listAll() {
        LOG.info("Searching all the projects...");
        return projectService.listAll();
    }

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

    @POST
    @RolesAllowed("admin")
    public Response create(Project project) {
        LOG.info("Creating a project.");
        Project created = projectService.create(project);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

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