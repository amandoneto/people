package org.acme.service;

import java.util.concurrent.CompletionStage;

import org.acme.dto.ProjectDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaMessageProducer {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("project-topic-out")
    Emitter<String> emitter;

    public CompletionStage<Void> send(ProjectDTO project) {
        try {
            return emitter.send(objectMapper.writeValueAsString(project));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize project message.", exception);
        }
    }
}
