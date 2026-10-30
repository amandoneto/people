package org.acme.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/** Composite identifier for an employee-skill association. */
@Embeddable
public class EmployeeSkillId implements Serializable {

    /** Employee identifier component. */
    @Column(name = "employee_id")
    public UUID employeeId;

    /** Skill identifier component. */
    @Column(name = "skill_id")
    public UUID skillId;

    /** Creates an uninitialized composite identifier for persistence frameworks. */
    public EmployeeSkillId() {
    }

    /**
     * Creates an identifier from its employee and skill components.
     *
     * @param employeeId employee identifier
     * @param skillId skill identifier
     */
    public EmployeeSkillId(UUID employeeId, UUID skillId) {
        this.employeeId = employeeId;
        this.skillId = skillId;
    }

    /**
     * Compares both identifier components.
     *
     * @param o object to compare
     * @return {@code true} when the other object has the same employee and skill identifiers
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EmployeeSkillId that = (EmployeeSkillId) o;
        return Objects.equals(employeeId, that.employeeId) && Objects.equals(skillId, that.skillId);
    }

    /**
     * Computes a hash from both identifier components.
     *
     * @return hash code for this composite identifier
     */
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, skillId);
    }
}
