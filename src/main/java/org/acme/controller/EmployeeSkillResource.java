package org.acme.controller;

import org.acme.model.Employee;
import org.acme.model.EmployeeSkill;
import org.acme.model.EmployeeSkillId;
import org.acme.model.Skill;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/employee-skills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeSkillResource {

    @GET
    public List<EmployeeSkill> listAll() {
        return EmployeeSkill.listAll();
    }

    @POST
    @Transactional
    public Response create(EmployeeSkill employeeSkill) {
        // Valida se o funcionário e a skill existem antes de associar
        Employee employee = Employee.findById(employeeSkill.employee.id);
        Skill skill = Skill.findById(employeeSkill.skill.id);

        if (employee == null || skill == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Employee ou Skill não encontrados.")
                    .build();
        }

        // Cria a chave composta
        EmployeeSkillId id = new EmployeeSkillId(employeeSkill.employee.id, employeeSkill.skill.id);

        // Instancia e preenche a entidade
        EmployeeSkill newEmployeeSkill = new EmployeeSkill();
        newEmployeeSkill.id = id;
        newEmployeeSkill.employee = employee;
        newEmployeeSkill.skill = skill;
        newEmployeeSkill.proficiencyLevel = employeeSkill.proficiencyLevel;

        newEmployeeSkill.persist();

        return Response.status(Response.Status.CREATED).entity(employeeSkill).build();
    }

    @DELETE
    @Path("/{employeeId}/{skillId}")
    @Transactional
    public Response delete(@PathParam("employeeId") UUID employeeId, @PathParam("skillId") UUID skillId) {
        EmployeeSkillId id = new EmployeeSkillId(employeeId, skillId);
        EmployeeSkill entity = EmployeeSkill.findById(id);

        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        entity.delete();
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
