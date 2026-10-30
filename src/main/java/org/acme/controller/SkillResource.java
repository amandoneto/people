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

/** REST endpoints for managing skills. */
@Path("/api/skills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class SkillResource {

    /** Creates the skill resource. */
    public SkillResource() {
    }

    @Inject
    SkillService skillService;

    /**
     * Lists skills on the requested page.
     *
     * @param page zero-based page index
     * @param pageSize maximum number of skills per page
     * @return a page of skills and pagination metadata
     */
    @GET
    @RolesAllowed({ "admin", "user" })
    public PaginatedResponse<Skill> listAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("5") int pageSize) {
        return skillService.listAll(page, pageSize);
    }

    /**
     * Finds a skill by its identifier.
     *
     * @param id skill identifier
     * @return the skill, or a not-found response
     */
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

    /**
     * Creates a skill.
     *
     * @param skill skill data
     * @return a created response containing the skill
     */
    @POST
    @RolesAllowed("admin")
    public Response create(Skill skill) {
        Skill created = skillService.create(skill);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Creates multiple skills.
     *
     * @param skills skill data to persist
     * @return a created response
     */
    @POST
    @Path("/list")
    @RolesAllowed("admin")
    public Response create(List<Skill> skills) {

        skillService.createAll(skills);
        return Response.status(Response.Status.CREATED).build();
    }

    /**
     * Updates a skill.
     *
     * @param id skill identifier
     * @param updatedSkill replacement skill data
     * @return the updated skill, or a not-found response
     */
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

    /**
     * Deletes a skill.
     *
     * @param id skill identifier
     * @return a no-content response, or a not-found response
     */
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