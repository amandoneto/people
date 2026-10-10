package org.acme.controller;

import java.util.concurrent.CompletionStage;

import org.acme.dto.ErrorResponse;
import org.acme.dto.ProjectDTO;
import org.acme.service.KafkaMessageProducer;
import org.jboss.logging.Logger;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import io.quarkus.security.Authenticated;

@Path("/api/kafka/projects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class KafkaMessageResource {

    private static final Logger LOG = Logger.getLogger(KafkaMessageResource.class);

    @Inject
    KafkaMessageProducer producer;

    @POST
    @RolesAllowed("admin")
    public CompletionStage<Response> publish(@Valid ProjectDTO project) {
        return producer.send(project)
                .thenApply(ignored -> Response.accepted().build())
                .exceptionally(exception -> {
                    LOG.error("Failed to publish project to Kafka topic 'project_topic'. "
                            + "Verify the topic exists and the Kafka broker is reachable.", exception);
                    ErrorResponse error = new ErrorResponse(
                            Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
                            "Unable to publish project. Kafka topic 'project_topic' may not exist or the broker is unavailable.");
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                            .entity(error)
                            .build();
                });
    }
}
