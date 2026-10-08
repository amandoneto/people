package org.acme.controller;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import org.acme.dto.EmployeeCreateRequest;
import org.acme.dto.EmployeeUpdateRequest;
import org.acme.dto.PaginatedResponse;
import org.acme.model.Employee;
import org.acme.service.EmployeeService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

@Path("/api/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class EmployeeResource {

    @Inject
    EmployeeService employeeService;

    @GET
    @RolesAllowed({ "admin", "user" })
    public PaginatedResponse<Employee> listAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("5") int pageSize) {
        return employeeService.listAll(page, pageSize);
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response getById(@PathParam("id") UUID id) {
        Employee employee = employeeService.getActiveById(id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(employee).build();
    }

    @POST
    @PermitAll
    public Response create(@Valid EmployeeCreateRequest request) {
        Employee employee = employeeService.create(request);
        return Response.status(Response.Status.CREATED).entity(employee).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response update(@PathParam("id") UUID id, EmployeeUpdateRequest request) {
        Employee employee = employeeService.update(id, request);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(employee).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = employeeService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}