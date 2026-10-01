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

@Entity
@Immutable
@Table(name = "tb_employee_event", schema = "talent", uniqueConstraints = {
        @UniqueConstraint(name = "uq_tb_employee_event_stream_version", columnNames = { "employee_id",
                "stream_version" })
})
public class EmployeeEvent {

    @Id
    @Column(name = "event_id", nullable = false, updatable = false)
    public UUID eventId;

    @Column(name = "employee_id", nullable = false, updatable = false)
    public UUID employeeId;

    @Column(name = "stream_version", nullable = false, updatable = false)
    public long streamVersion;

    @Column(name = "event_type", nullable = false, length = 100, updatable = false)
    public String eventType;

    @Column(name = "event_version", nullable = false, updatable = false)
    public short eventVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, updatable = false)
    public Map<String, Object> payload;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    public Instant occurredAt;

    @Column(name = "actor_id", length = 255, updatable = false)
    public String actorId;

    @Column(name = "correlation_id", length = 255, updatable = false)
    public String correlationId;
}