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

/** REST endpoints for employee-skill associations. */
@Path("/api/employee-skills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class EmployeeSkillResource {

    /** Creates the employee-skill resource. */
    public EmployeeSkillResource() {
    }

    @Inject
    EmployeeSkillService service;

    /**
     * Lists employee-skill associations for active employees.
     *
     * @return all employee-skill associations for active employees
     */
    @GET
    @RolesAllowed({ "admin", "user" })
    public List<EmployeeSkill> listAll() {
        return service.listAll();
    }

    /**
     * Groups employee names by the supplied skill names.
     *
     * @param skillNames skill names to search for
     * @return the requested skills mapped to matching employee names
     */
    @POST
    @Path("/search")
    @RolesAllowed("admin")
    public Map<String, List<String>> findEmployeesBySkills(List<String> skillNames) {
        return service.findEmployeesBySkills(skillNames);
    }

    /**
     * Creates an employee-skill association.
     *
     * @param employeeSkill association data
     * @return a created response, or a bad-request response if either entity is missing
     */
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

    /**
     * Deletes an association by employee and skill identifiers.
     *
     * @param employeeId employee identifier
     * @param skillId skill identifier
     * @return a no-content response, or a not-found response
     */
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
