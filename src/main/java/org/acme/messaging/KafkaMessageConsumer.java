package org.acme.messaging;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.acme.dto.ProjectDTO;
import org.acme.model.Project;
import org.acme.service.ProjectService;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

@ApplicationScoped
public class KafkaMessageConsumer {

    private static final Logger LOG = Logger.getLogger(KafkaMessageConsumer.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    Validator validator;

    @Inject
    ProjectService projectService;

    @Incoming("project-topic-in")
    public void consume(String message) {
        ProjectDTO projectDTO;
        try {
            projectDTO = objectMapper.readValue(message, ProjectDTO.class);
        } catch (IOException exception) {
            LOG.error("Rejected malformed project message.", exception);
            throw new IllegalArgumentException("Project message must contain valid JSON.", exception);
        }

        Set<ConstraintViolation<ProjectDTO>> violations = validator.validate(projectDTO);
        if (!violations.isEmpty()) {
            String details = violations.stream()
                    .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                    .collect(Collectors.joining(", "));
            LOG.errorf("Rejected invalid project message: %s", details);
            throw new ConstraintViolationException("Invalid project message.", violations);
        }

        Project project = projectService.createOrUpdate(projectDTO.toProject());
        LOG.infov("Created or updated project {0}.", project.id);
    }
}
