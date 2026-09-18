package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "tb_employee_skill", schema = "talent")
public class EmployeeSkill extends PanacheEntityBase {

    @EmbeddedId
    public EmployeeSkillId id = new EmployeeSkillId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    public Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id")
    public Skill skill;

    @Column(name = "proficiency_level", nullable = false, length = 30)
    public String proficiencyLevel;
}
