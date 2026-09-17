package org.acme.controller;

import org.acme.model.Skill;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/skills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SkillResource {

    @GET
    public List<Skill> listAll() {
        return Skill.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        Skill skill = Skill.findById(id);
        if (skill == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(skill).build();
    }

    @POST
    @Transactional
    public Response create(Skill skill) {
        skill.id = null;
        skill.persist();
        return Response.status(Response.Status.CREATED).entity(skill).build();
    }

    @PUT
    @Path("/{id}")
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
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = Skill.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}