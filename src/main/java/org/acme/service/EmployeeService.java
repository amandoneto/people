package org.acme.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dto.EmployeeCreateRequest;
import org.acme.dto.EmployeeUpdateRequest;
import org.acme.dto.PaginatedResponse;
import org.acme.model.Employee;
import org.acme.model.EmployeeCredential;
import org.acme.model.EmployeeEvent;
import org.acme.model.EmployeeEventPayload;
import org.acme.repository.EmployeeCredentialRepository;
import org.acme.repository.EmployeeEventRepository;
import org.acme.repository.EmployeeRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Employee profile operations backed by an append-only event stream. */
@ApplicationScoped
public class EmployeeService {

    /** Creates the employee service. */
    public EmployeeService() {
    }

    private static final short EVENT_VERSION = 1;
    private static final TypeReference<Map<String, Object>> PAYLOAD_MAP = new TypeReference<>() {
    };

    @Inject
    EmployeeEventRepository eventRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    EmployeeCredentialRepository credentialRepository;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    SecurityIdentity securityIdentity;

    /**
     * Creates an employee, stores a password hash, and appends the creation event.
     *
     * @param request employee profile and password
     * @return the created employee
     */
    @Transactional
    public Employee create(EmployeeCreateRequest request) {
        Employee employee = new Employee();
        employee.name = request.name();
        employee.email = request.email().toLowerCase(java.util.Locale.ROOT);
        employee.role = request.role();
        employee.seniority = request.seniority();
        employee.createdAt = LocalDateTime.now();
        employee.updatedAt = employee.createdAt;
        employeeRepository.persist(employee);
        employeeRepository.flush();

        EmployeeCredential credential = new EmployeeCredential();
        credential.employeeId = employee.id;
        credential.passwordHash = AuthenticationService.hashPassword(request.password());
        credentialRepository.persist(credential);

        appendEvent(employee, "EmployeeCreated", null);
        employeeRepository.flush();
        return employee;
    }

    /**
     * Updates an active employee and appends a profile-update event.
     *
     * @param employeeId employee identifier
     * @param request replacement profile values
     * @return the updated employee, or {@code null} if it is missing or deleted
     */
    @Transactional
    public Employee update(UUID employeeId, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findByIdForUpdate(employeeId);
        if (employee == null || employee.deletedAt != null) {
            return null;
        }

        employee.name = request.name();
        employee.email = request.email().toLowerCase(java.util.Locale.ROOT);
        employee.role = request.role();
        employee.seniority = request.seniority();
        employee.updatedAt = LocalDateTime.now();
        appendEvent(employee, "EmployeeProfileUpdated", null);
        employeeRepository.flush();
        return employee;
    }

    /**
     * Soft-deletes an active employee and appends a deletion event.
     *
     * @param employeeId employee identifier
     * @return {@code true} if the employee was deleted
     */
    @Transactional
    public boolean delete(UUID employeeId) {
        Employee employee = employeeRepository.findByIdForUpdate(employeeId);
        if (employee == null || employee.deletedAt != null) {
            return false;
        }

        employee.deletedAt = Instant.now();
        appendEvent(employee, "EmployeeDeleted", employee.deletedAt);
        employeeRepository.flush();
        return true;
    }

    /**
     * Reconstructs an employee's state from its ordered event stream.
     *
     * @param employeeId employee stream identifier
     * @return replayed state, or {@code null} if the stream has no events
     * @throws IllegalStateException if the stream contains an unsupported, invalid, or missing version
     */
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

    /**
     * Rebuilds the persisted employee projection from its event stream.
     *
     * @param employeeId employee identifier
     * @return {@code true} if the projection was rebuilt, or {@code false} if the employee is missing
     * @throws IllegalStateException if no event stream exists or it cannot be replayed
     */
    @Transactional
    public boolean rebuildProjection(UUID employeeId) {
        Employee employee = employeeRepository.findByIdForUpdate(employeeId);
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
        employeeRepository.flush();
        return true;
    }

    /**
     * Lists active employees on a page.
     *
     * @param page zero-based page index
     * @param pageSize maximum number of employees per page
     * @return active employees with pagination metadata
     */
    public PaginatedResponse<Employee> listAll(int page, int pageSize) {
        long totalRecords = employeeRepository.countActive();
        return PaginatedResponse.of(
                employeeRepository.findActivePage(page, pageSize),
                totalRecords,
                page,
                pageSize);
    }

    /**
     * Finds an active employee by identifier.
     *
     * @param employeeId employee identifier
     * @return active employee, or {@code null} if none exists
     */
    public Employee getActiveById(UUID employeeId) {
        return employeeRepository.findActiveById(employeeId);
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