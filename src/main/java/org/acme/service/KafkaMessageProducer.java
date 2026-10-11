package org.acme.service;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.acme.dto.ProjectDTO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaMessageProducer {

    private static final Logger LOG = Logger.getLogger(KafkaMessageProducer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("project-topic-out")
    Emitter<String> emitter;

    @ConfigProperty(name = "kafka.project.publish.max-retries", defaultValue = "3")
    long maxRetries;

    @ConfigProperty(name = "kafka.project.publish.timeout", defaultValue = "PT14S")
    Duration publishTimeout;

    @ConfigProperty(name = "kafka.project.publish.retry.initial-delay", defaultValue = "PT1S")
    Duration initialRetryDelay;

    @ConfigProperty(name = "kafka.project.publish.retry.max-delay", defaultValue = "PT4S")
    Duration maxRetryDelay;

    public CompletionStage<Void> send(ProjectDTO project) {
        try {
            ProjectDTO projectWithStableId = project.id() == null
                    ? new ProjectDTO(UUID.randomUUID(), project.name(), project.description(), project.status())
                    : project;
            String message = objectMapper.writeValueAsString(projectWithStableId);
            AtomicLong attempt = new AtomicLong();
            return Uni.createFrom()
                    .completionStage(() -> emitter.send(message))
                    .onFailure()
                    .invoke(failure -> LOG.warnf(
                            "Kafka publish attempt %d/%d failed for topic 'project_topic': %s",
                            attempt.incrementAndGet(),
                            maxRetries + 1,
                            failure))
                    .onFailure()
                    .retry()
                    .withBackOff(initialRetryDelay, maxRetryDelay)
                    .atMost(maxRetries)
                    .ifNoItem()
                    .after(publishTimeout)
                    .fail()
                    .subscribeAsCompletionStage();
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize project message.", exception);
        }
    }
}
