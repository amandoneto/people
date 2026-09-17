package org.acme.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class EmployeeSkillId implements Serializable {

    @Column(name = "employee_id")
    public UUID employeeId;

    @Column(name = "skill_id")
    public UUID skillId;

    public EmployeeSkillId() {
    }

    public EmployeeSkillId(UUID employeeId, UUID skillId) {
        this.employeeId = employeeId;
        this.skillId = skillId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EmployeeSkillId that = (EmployeeSkillId) o;
        return Objects.equals(employeeId, that.employeeId) && Objects.equals(skillId, that.skillId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, skillId);
    }
}
