package org.acme.controller;

import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import org.acme.dto.PaginatedResponse;
import org.acme.dto.EmployeeCreateRequest;
import org.acme.dto.EmployeeUpdateRequest;
import org.acme.model.Employee;
import org.acme.service.EmployeeService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
        PanacheQuery<Employee> query = Employee.find("deletedAt is null");
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
        Employee employee = Employee.find("id = ?1 and deletedAt is null", id).firstResult();
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(employee).build();
    }

    @POST
    @RolesAllowed("admin")
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