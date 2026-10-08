package org.acme.controller;

import io.quarkus.security.Authenticated;
import org.acme.model.Allocation;
import org.acme.service.AllocationService;
import jakarta.inject.Inject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/allocations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class AllocationResource {

    @Inject
    AllocationService allocationService;

    @GET
    @RolesAllowed({ "admin", "user" })
    public List<Allocation> listAll() {
        return allocationService.listAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response getById(@PathParam("id") UUID id) {
        Allocation allocation = allocationService.getById(id);
        if (allocation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(allocation).build();
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Allocation allocation) {
        Allocation created = allocationService.create(allocation);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "admin", "user" })
    public Response update(@PathParam("id") UUID id, Allocation updatedAllocation) {
        Allocation allocation = allocationService.update(id, updatedAllocation);
        if (allocation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(allocation).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("admin")
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = allocationService.delete(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}