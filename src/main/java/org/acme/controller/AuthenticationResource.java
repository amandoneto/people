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

/** REST endpoint for employee authentication. */
@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    /**
     * Creates the resource with its authentication service.
     *
     * @param authenticationService service that verifies credentials and creates tokens
     */
    @Inject
    public AuthenticationResource(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Authenticates an employee and returns a JWT on success.
     *
     * @param request validated login credentials
     * @return an OK response with the login result, or an unauthorized response
     */
    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request) {
        return authenticationService.authenticate(request)
                .map(auth -> Response.ok(new LoginResponse(auth.token(), auth.name(), auth.seniority())).build())
                .orElseGet(() -> Response.status(Response.Status.UNAUTHORIZED).build());
    }
}