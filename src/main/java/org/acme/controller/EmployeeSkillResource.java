package org.acme.controller;

import io.quarkus.security.Authenticated;
import org.acme.model.EmployeeSkill;
import org.acme.service.EmployeeSkillService;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Path("/api/employee-skills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class EmployeeSkillResource {

    @Inject
    EmployeeSkillService service;

    @GET
    @RolesAllowed({ "admin", "user" })
    public List<EmployeeSkill> listAll() {
        return service.listAll();
    }

    @POST
    @Path("/search")
    @RolesAllowed("admin")
    public Map<String, List<String>> findEmployeesBySkills(List<String> skillNames) {
        return service.findEmployeesBySkills(skillNames);
    }

    @POST
    @RolesAllowed("admin")
    public Response create(EmployeeSkill employeeSkill) {
        EmployeeSkill createdEmployeeSkill = service.create(employeeSkill);
        if (createdEmployeeSkill == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Employee ou Skill não encontrados.")
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(createdEmployeeSkill).build();
    }

    @DELETE
    @Path("/{employeeId}/{skillId}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("employeeId") UUID employeeId, @PathParam("skillId") UUID skillId) {
        if (!service.delete(employeeId, skillId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
