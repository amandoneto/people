package org.acme.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.List;

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

    public static List<EmployeeSkill> findBySkillNames(List<String> skillNames) {
        if (skillNames == null || skillNames.isEmpty()) {
            return List.of();
        }

        return find(
                "select distinct es from EmployeeSkill es "
                        + "join fetch es.skill "
                        + "join fetch es.employee "
                        + "where es.skill.name in ?1",
                skillNames)
                .list();
    }

}
