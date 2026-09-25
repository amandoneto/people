package org.acme.controller;

import io.quarkus.security.Authenticated;
import org.acme.dto.PaginatedResponse;
import org.acme.model.Skill;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
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

    @GET
    @RolesAllowed({ "admin", "user" })
    public PaginatedResponse<Skill> listAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("5") int pageSize) {
        PanacheQuery<Skill> query = Skill.findAll();
        long totalRecords = query.count();
        query.page(page, pageSize);
        int totalPages = query.pageCount();

        return new PaginatedResponse<>(
                query.list(),
                totalRecords,
                page,
                pageSize,
                totalPages,
                page < totalPages - 1 ? page + 1 : null,
                page > 0 && totalPages > 0 ? page - 1 : null);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response getById(@PathParam("id") UUID id) {
        Skill skill = Skill.findById(id);
        if (skill == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(skill).build();
    }

    @POST
    @RolesAllowed("admin")
    @Transactional
    public Response create(Skill skill) {
        skill.id = null;
        skill.persist();
        return Response.status(Response.Status.CREATED).entity(skill).build();
    }

    @POST
    @Transactional
    @Path("/list")
    @RolesAllowed("admin")
    public Response create(List<Skill> skills) {

        Skill.persist(skills);
        return Response.status(Response.Status.CREATED).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    @Transactional
    public Response update(@PathParam("id") UUID id, Skill updatedSkill) {
        Skill skill = Skill.findById(id);
        if (skill == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        skill.name = updatedSkill.name;
        skill.category = updatedSkill.category;

        return Response.ok(skill).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = Skill.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}