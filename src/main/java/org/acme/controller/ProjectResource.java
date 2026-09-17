package org.acme.controller;

import org.acme.model.Project;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @GET
    public List<Project> listAll() {
        return Project.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        Project project = Project.findById(id);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(project).build();
    }

    @POST
    @Transactional
    public Response create(Project project) {
        project.id = null;
        project.persist();
        return Response.status(Response.Status.CREATED).entity(project).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") UUID id, Project updatedProject) {
        Project project = Project.findById(id);
        if (project == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        project.name = updatedProject.name;
        project.description = updatedProject.description;
        project.status = updatedProject.status;

        return Response.ok(project).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = Project.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}