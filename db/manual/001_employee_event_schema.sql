BEGIN;

ALTER TABLE talent.tb_employee
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN deleted_at TIMESTAMPTZ,
    ALTER COLUMN password DROP NOT NULL;

CREATE TABLE talent.tb_employee_event (
    event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id UUID NOT NULL REFERENCES talent.tb_employee(id),
    stream_version BIGINT NOT NULL CHECK (stream_version > 0),
    event_type VARCHAR(100) NOT NULL,
    event_version SMALLINT NOT NULL CHECK (event_version > 0),
    payload JSONB NOT NULL CHECK (jsonb_typeof(payload) = 'object'),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    actor_id VARCHAR(255),
    correlation_id VARCHAR(255),
    CONSTRAINT uq_tb_employee_event_stream_version
        UNIQUE (employee_id, stream_version)
);

CREATE TABLE talent.tb_employee_credential (
    employee_id UUID PRIMARY KEY REFERENCES talent.tb_employee(id),
    password_hash VARCHAR(255) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMIT;