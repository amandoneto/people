package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Employee;
import org.acme.model.EmployeeSkill;
import org.acme.model.EmployeeSkillId;
import org.acme.model.Skill;

import java.util.List;
import java.util.UUID;

/**
 * Provides persistence operations for employee-skill associations.
 */
@ApplicationScoped
public class EmployeeSkillRepository implements PanacheRepository<EmployeeSkill> {

    /**
     * Lists associations with their employee and skill relationships initialized.
     *
     * @return all employee-skill associations
     */
    @Override
    public List<EmployeeSkill> listAll() {
        return find(
                "select distinct es from EmployeeSkill es "
                        + "join fetch es.skill "
                        + "join fetch es.employee")
                .list();
    }

    /**
     * Finds associations for the supplied skill names and fetches their related
     * entities.
     *
     * @param skillNames skill names to search for
     * @return matching employee-skill associations
     */
    public List<EmployeeSkill> findBySkillNames(List<String> skillNames) {
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

    /**
     * Finds an employee by identifier for association validation.
     *
     * @param employeeId employee identifier
     * @return the employee, or {@code null} when it does not exist
     */
    public Employee findEmployeeById(UUID employeeId) {
        return Employee.findById(employeeId);
    }

    /**
     * Finds a skill by identifier for association validation.
     *
     * @param skillId skill identifier
     * @return the skill, or {@code null} when it does not exist
     */
    public Skill findSkillById(UUID skillId) {
        return Skill.findById(skillId);
    }

    /**
     * Finds an employee-skill association by its composite identifier.
     *
     * @param id composite association identifier
     * @return the association, or {@code null} when it does not exist
     */
    public EmployeeSkill findById(EmployeeSkillId id) {
        return find("id", id).firstResult();
    }
}