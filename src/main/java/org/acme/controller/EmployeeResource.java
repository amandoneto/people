package org.acme.controller;

import org.acme.dto.PaginatedResponse;
import org.acme.model.Employee;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.UUID;

@Path("/api/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {

    @GET
    public PaginatedResponse<Employee> listAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("pageSize") @DefaultValue("5") int pageSize) {
        PanacheQuery<Employee> query = Employee.findAll();
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
    public Response getById(@PathParam("id") UUID id) {
        Employee employee = Employee.findById(id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(employee).build();
    }

    @POST
    @Transactional
    public Response create(Employee employee) {
        employee.id = null; // Garante que o gerador de UUIDv7 atue
        employee.createdAt = LocalDateTime.now();
        employee.updatedAt = LocalDateTime.now();
        employee.persist();
        return Response.status(Response.Status.CREATED).entity(employee).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") UUID id, Employee updatedEmployee) {
        Employee employee = Employee.findById(id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        employee.name = updatedEmployee.name;
        employee.email = updatedEmployee.email;
        employee.role = updatedEmployee.role;
        employee.seniority = updatedEmployee.seniority;
        employee.updatedAt = LocalDateTime.now();

        return Response.ok(employee).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = Employee.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}