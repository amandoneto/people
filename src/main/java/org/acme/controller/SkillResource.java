package org.acme.controller;

import io.quarkus.security.Authenticated;
import org.acme.dto.PaginatedResponse;
import org.acme.model.Skill;
import org.acme.service.SkillService;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/skills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class SkillResource {

    @Inject
    SkillService skillService;

    @GET
    @RolesAllowed({ "admin", "user" })
    public PaginatedResponse<Skill> listAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("5") int pageSize) {
        return skillService.listAll(page, pageSize);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response getById(@PathParam("id") UUID id) {
        Skill skill = skillService.getById(id);
        if (skill == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(skill).build();
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Skill skill) {
        Skill created = skillService.create(skill);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @POST
    @Path("/list")
    @RolesAllowed("admin")
    public Response create(List<Skill> skills) {

        skillService.createAll(skills);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response update(@PathParam("id") UUID id, Skill updatedSkill) {
        Skill skill = skillService.update(id, updatedSkill);
        if (skill == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(skill).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = skillService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}