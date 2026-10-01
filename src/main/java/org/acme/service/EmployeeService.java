package org.acme.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.acme.dto.EmployeeCreateRequest;
import org.acme.dto.EmployeeUpdateRequest;
import org.acme.model.Employee;
import org.acme.model.EmployeeCredential;
import org.acme.model.EmployeeEvent;
import org.acme.model.EmployeeEventPayload;
import org.acme.repository.EmployeeCredentialRepository;
import org.acme.repository.EmployeeEventRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class EmployeeService {

    private static final short EVENT_VERSION = 1;
    private static final TypeReference<Map<String, Object>> PAYLOAD_MAP = new TypeReference<>() {
    };

    @Inject
    EntityManager entityManager;

    @Inject
    EmployeeEventRepository eventRepository;

    @Inject
    EmployeeCredentialRepository credentialRepository;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    SecurityIdentity securityIdentity;

    @Transactional
    public Employee create(EmployeeCreateRequest request) {
        Employee employee = new Employee();
        employee.name = request.name();
        employee.email = request.email().toLowerCase(java.util.Locale.ROOT);
        employee.role = request.role();
        employee.seniority = request.seniority();
        employee.createdAt = LocalDateTime.now();
        employee.updatedAt = employee.createdAt;
        entityManager.persist(employee);
        entityManager.flush();

        EmployeeCredential credential = new EmployeeCredential();
        credential.employeeId = employee.id;
        credential.passwordHash = AuthenticationService.hashPassword(request.password());
        credentialRepository.persist(credential);

        appendEvent(employee, "EmployeeCreated", null);
        entityManager.flush();
        return employee;
    }

    @Transactional
    public Employee update(UUID employeeId, EmployeeUpdateRequest request) {
        Employee employee = entityManager.find(Employee.class, employeeId, LockModeType.PESSIMISTIC_WRITE);
        if (employee == null || employee.deletedAt != null) {
            return null;
        }

        employee.name = request.name();
        employee.email = request.email().toLowerCase(java.util.Locale.ROOT);
        employee.role = request.role();
        employee.seniority = request.seniority();
        employee.updatedAt = LocalDateTime.now();
        appendEvent(employee, "EmployeeProfileUpdated", null);
        entityManager.flush();
        return employee;
    }

    @Transactional
    public boolean delete(UUID employeeId) {
        Employee employee = entityManager.find(Employee.class, employeeId, LockModeType.PESSIMISTIC_WRITE);
        if (employee == null || employee.deletedAt != null) {
            return false;
        }

        employee.deletedAt = Instant.now();
        appendEvent(employee, "EmployeeDeleted", employee.deletedAt);
        entityManager.flush();
        return true;
    }

    @Transactional
    public EmployeeReplay replay(UUID employeeId) {
        List<EmployeeEvent> events = eventRepository.findStream(employeeId);
        EmployeeEventPayload state = null;
        long expectedVersion = 1;

        for (EmployeeEvent event : events) {
            if (event.streamVersion != expectedVersion || event.eventVersion != EVENT_VERSION) {
                throw new IllegalStateException("Employee event stream has an unsupported or missing version");
            }
            EmployeeEventPayload nextState = objectMapper.convertValue(event.payload, EmployeeEventPayload.class);
            if (!employeeId.equals(nextState.employeeId())) {
                throw new IllegalStateException("Employee event payload identifier does not match its stream");
            }

            switch (event.eventType) {
                case "EmployeeCreated", "EmployeeImported" -> {
                    if (state != null || nextState.deletedAt() != null) {
                        throw new IllegalStateException("Employee creation event is invalid at this stream position");
                    }
                }
                case "EmployeeProfileUpdated" -> {
                    if (state == null || state.deletedAt() != null || nextState.deletedAt() != null) {
                        throw new IllegalStateException("Employee update event is invalid at this stream position");
                    }
                }
                case "EmployeeDeleted" -> {
                    if (state == null || state.deletedAt() != null || nextState.deletedAt() == null) {
                        throw new IllegalStateException("Employee delete event is invalid at this stream position");
                    }
                }
                default -> throw new IllegalStateException("Unknown Employee event type: " + event.eventType);
            }

            state = nextState;
            expectedVersion++;
        }

        return state == null ? null : new EmployeeReplay(expectedVersion - 1, state);
    }

    @Transactional
    public boolean rebuildProjection(UUID employeeId) {
        Employee employee = entityManager.find(Employee.class, employeeId, LockModeType.PESSIMISTIC_WRITE);
        if (employee == null) {
            return false;
        }

        EmployeeReplay replay = replay(employeeId);
        if (replay == null) {
            throw new IllegalStateException("Employee projection has no event stream");
        }

        EmployeeEventPayload state = replay.state();
        employee.name = state.name();
        employee.email = state.email();
        employee.role = state.role();
        employee.seniority = state.seniority();
        employee.createdAt = state.createdAt();
        employee.updatedAt = state.updatedAt();
        employee.deletedAt = state.deletedAt();
        employee.version = replay.version();
        entityManager.flush();
        return true;
    }

    private void appendEvent(Employee employee, String eventType, Instant deletedAt) {
        employee.version++;
        EmployeeEventPayload payload = new EmployeeEventPayload(
                employee.id,
                employee.name,
                employee.email,
                employee.role,
                employee.seniority,
                employee.createdAt,
                employee.updatedAt,
                deletedAt);

        EmployeeEvent event = new EmployeeEvent();
        event.eventId = UUID.randomUUID();
        event.employeeId = employee.id;
        event.streamVersion = employee.version;
        event.eventType = eventType;
        event.eventVersion = EVENT_VERSION;
        event.payload = objectMapper.convertValue(payload, PAYLOAD_MAP);
        event.occurredAt = Instant.now();
        event.actorId = securityIdentity.isAnonymous() ? null : securityIdentity.getPrincipal().getName();
        eventRepository.persist(event);
    }
}