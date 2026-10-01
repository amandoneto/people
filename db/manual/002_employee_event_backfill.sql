BEGIN;

INSERT INTO talent.tb_employee_credential (employee_id, password_hash)
SELECT e.id, e.password
FROM talent.tb_employee AS e
WHERE e.deleted_at IS NULL
ON CONFLICT (employee_id) DO NOTHING;

INSERT INTO talent.tb_employee_event (
    employee_id,
    stream_version,
    event_type,
    event_version,
    payload,
    occurred_at
)
SELECT
    e.id,
    1,
    'EmployeeImported',
    1,
    jsonb_build_object(
        'employeeId', e.id,
        'name', e.name,
        'email', e.email,
        'role', e.role,
        'seniority', e.seniority,
        'createdAt', e.created_at,
        'updatedAt', e.updated_at,
        'deletedAt', NULL
    ),
    CURRENT_TIMESTAMP
FROM talent.tb_employee AS e
WHERE e.deleted_at IS NULL
  AND e.version = 0
ON CONFLICT (employee_id, stream_version) DO NOTHING;

UPDATE talent.tb_employee AS e
SET version = 1
WHERE e.deleted_at IS NULL
  AND e.version = 0
  AND EXISTS (
      SELECT 1
      FROM talent.tb_employee_event AS ev
      WHERE ev.employee_id = e.id
        AND ev.stream_version = 1
        AND ev.event_type = 'EmployeeImported'
  );

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM talent.tb_employee AS e
        WHERE e.deleted_at IS NULL
          AND (
              e.version <> 1
              OR NOT EXISTS (
                  SELECT 1 FROM talent.tb_employee_credential AS c
                  WHERE c.employee_id = e.id
              )
              OR NOT EXISTS (
                  SELECT 1 FROM talent.tb_employee_event AS ev
                  WHERE ev.employee_id = e.id
                    AND ev.stream_version = 1
                    AND ev.event_type = 'EmployeeImported'
              )
          )
    ) THEN
        RAISE EXCEPTION 'Employee event backfill verification failed';
    END IF;
END
$$;

COMMIT;