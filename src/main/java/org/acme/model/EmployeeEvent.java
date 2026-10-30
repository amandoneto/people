package org.acme.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Immutable event recorded in an employee's versioned event stream. */
@Entity
@Immutable
@Table(name = "tb_employee_event", schema = "talent", uniqueConstraints = {
        @UniqueConstraint(name = "uq_tb_employee_event_stream_version", columnNames = { "employee_id",
                "stream_version" })
})
public class EmployeeEvent {

    /** Creates an empty employee event entity. */
    public EmployeeEvent() {
    }

    /** Unique identifier for this event. */
    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    public UUID eventId;

    /** Identifier of the employee stream containing this event. */
    @Column(name = "employee_id", nullable = false, updatable = false)
    public UUID employeeId;

    /** One-based position of this event in the employee stream. */
    @Column(name = "stream_version", nullable = false, updatable = false)
    public long streamVersion;

    /** Name identifying the kind of employee event. */
    @Column(name = "event_type", nullable = false, length = 100, updatable = false)
    public String eventType;

    /** Schema version used to interpret this event's payload. */
    @Column(name = "event_version", nullable = false, updatable = false)
    public short eventVersion;

    /** Event data serialized as JSON. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, updatable = false)
    public Map<String, Object> payload;

    /** Time when the event occurred. */
    @Column(name = "occurred_at", nullable = false, updatable = false)
    public Instant occurredAt;

    /** Name of the actor that caused the event, when available. */
    @Column(name = "actor_id", length = 255, updatable = false)
    public String actorId;

    /** Correlation identifier associated with the event, when available. */
    @Column(name = "correlation_id", length = 255, updatable = false)
    public String correlationId;
}