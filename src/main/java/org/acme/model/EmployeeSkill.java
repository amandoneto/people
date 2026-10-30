package org.acme.model;

import jakarta.persistence.*;

/** Association between an employee and a skill, including proficiency level. */
@Entity
@Table(name = "tb_employee_skill", schema = "talent")
public class EmployeeSkill {

    /** Creates an empty employee-skill association. */
    public EmployeeSkill() {
    }

    /** Composite employee and skill identifier. */
    @EmbeddedId
    public EmployeeSkillId id = new EmployeeSkillId();

    /** Employee in this association. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    public Employee employee;

    /** Skill in this association. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    public Skill skill;

    /** Employee's proficiency level for the associated skill. */
    @Column(name = "proficiency_level", nullable = false, length = 30)
    public String proficiencyLevel;

}
