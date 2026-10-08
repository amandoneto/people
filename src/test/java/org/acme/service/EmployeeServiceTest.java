package org.acme.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.acme.dto.EmployeeCreateRequest;
import org.acme.dto.EmployeeUpdateRequest;
import org.acme.model.Employee;
import org.acme.model.EmployeeCredential;
import org.acme.model.EmployeeEvent;
import org.acme.repository.EmployeeCredentialRepository;
import org.acme.repository.EmployeeEventRepository;
import org.acme.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class EmployeeServiceTest {

    @Inject
    EmployeeService service;

    @Inject
    EmployeeEventRepository eventRepository;

    @Inject
    EmployeeCredentialRepository credentialRepository;

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    EntityManager entityManager;

    @Test
    void commandsAppendReplayableEventsAndKeepCredentialsOutOfPayloads() {
        String email = "event." + UUID.randomUUID() + "@example.com";
        Employee employee = service.create(new EmployeeCreateRequest(
                "Event Employee", email, "Developer", "Senior", "TestPassword123!"));

        assertEquals(1, employee.version);
        EmployeeCredential credential = credentialRepository.findById(employee.id);
        assertNotNull(credential);
        assertFalse(credential.passwordHash.equals("TestPassword123!"));

        Employee updated = service.update(employee.id, new EmployeeUpdateRequest(
                "Updated Event Employee", email, "Staff Developer", "Principal"));
        assertEquals(2, updated.version);

        assertTrue(service.delete(employee.id));
        assertFalse(service.delete(employee.id));

        List<EmployeeEvent> events = eventRepository.findStream(employee.id);
        assertEquals(List.of("EmployeeCreated", "EmployeeProfileUpdated", "EmployeeDeleted"),
                events.stream().map(event -> event.eventType).toList());
        assertEquals(List.of(1L, 2L, 3L), events.stream().map(event -> event.streamVersion).toList());
        events.forEach(event -> {
            assertFalse(event.payload.containsKey("password"));
            assertFalse(event.payload.containsKey("passwordHash"));
        });

        EmployeeReplay replay = service.replay(employee.id);
        assertEquals(3, replay.version());
        assertEquals("Updated Event Employee", replay.state().name());
        assertNotNull(replay.state().deletedAt());
        assertNull(employeeRepository.findActiveById(employee.id));
        assertTrue(service.rebuildProjection(employee.id));
        assertEquals(3, entityManager.find(Employee.class, employee.id).version);
    }
}