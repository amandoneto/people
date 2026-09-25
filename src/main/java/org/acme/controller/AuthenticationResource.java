package org.acme.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.LoginRequest;
import org.acme.dto.LoginResponse;
import org.acme.service.AuthenticationService;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    @Inject
    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        return authenticationService.authenticate(request)
                .map(token -> Response.ok(new LoginResponse(token)).build())
                .orElseGet(() -> Response.status(Response.Status.UNAUTHORIZED).build());
    }
}