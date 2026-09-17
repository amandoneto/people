package org.acme.controller;

import org.acme.model.Allocation;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/api/allocations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AllocationResource {

    @GET
    public List<Allocation> listAll() {
        return Allocation.listAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        Allocation allocation = Allocation.findById(id);
        if (allocation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(allocation).build();
    }

    @POST
    @Transactional
    public Response create(Allocation allocation) {
        allocation.id = null;
        allocation.persist();
        return Response.status(Response.Status.CREATED).entity(allocation).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response update(@PathParam("id") UUID id, Allocation updatedAllocation) {
        Allocation allocation = Allocation.findById(id);
        if (allocation == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        allocation.employee = updatedAllocation.employee;
        allocation.project = updatedAllocation.project;
        allocation.allocationPercentage = updatedAllocation.allocationPercentage;
        allocation.startDate = updatedAllocation.startDate;
        allocation.endDate = updatedAllocation.endDate;

        return Response.ok(allocation).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") UUID id) {
        boolean deleted = Allocation.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}