package org.acme.controller;

import io.quarkus.security.Authenticated;
import org.acme.model.Project;
import org.jboss.logging.Logger;

import jakarta.transaction.Transactional;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class ProjectResource {

    // Criação do logger padrão da classe
    private static final Logger LOG = Logger.getLogger(ProjectResource.class);

    @GET
    @RolesAllowed({ "admin", "user" })
    public List<Project> listAll() {
        LOG.info("Searching all the projects...");
        return Project.listAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response getById(@PathParam("id") UUID id) {
        LOG.infov("Searching for project id {0}.", id);
        Project project = Project.findById(id);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(project).build();
    }

    @POST
    @RolesAllowed("admin")
    @Transactional
    public Response create(Project project) {
        LOG.info("Creating a project.");
        project.id = null;
        project.persist();
        return Response.status(Response.Status.CREATED).entity(project).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Transactional
    public Response update(@PathParam("id") UUID id, Project updatedProject) {
        LOG.infov("Updating project id {0}.", id);

        Project project = Project.findById(id);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        project.name = updatedProject.name;
        project.description = updatedProject.description;
        project.status = updatedProject.status;
        project.updatedAt = LocalDateTime.now();

        return Response.ok(project).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        LOG.infov("Deleting project id {0}.", id);
        boolean deleted = Project.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}