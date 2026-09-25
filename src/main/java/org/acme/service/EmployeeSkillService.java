package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.Employee;
import org.acme.model.EmployeeSkill;
import org.acme.model.EmployeeSkillId;
import org.acme.model.Skill;
import org.acme.repository.EmployeeSkillRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Contains business operations for employee-skill associations.
 */
@ApplicationScoped
public class EmployeeSkillService {

    private final EmployeeSkillRepository repository;

    /**
     * Creates a service using the employee-skill repository.
     *
     * @param repository repository used by the service
     */
    @Inject
    public EmployeeSkillService(EmployeeSkillRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns all employee-skill associations.
     *
     * @return all persisted associations
     */
    public List<EmployeeSkill> listAll() {
        return repository.listAll();
    }

    /**
     * Groups employee names by the requested skill name.
     *
     * @param skillNames skill names to search for
     * @return a map containing every requested skill name and its employees
     */
    public Map<String, List<String>> findEmployeesBySkills(List<String> skillNames) {
        Map<String, List<String>> employeesBySkill = new LinkedHashMap<>();
        if (skillNames == null) {
            return employeesBySkill;
        }

        skillNames.forEach(skillName -> employeesBySkill.put(skillName, new ArrayList<>()));
        repository.findBySkillNames(skillNames).forEach(employeeSkill -> {
            String skillName = employeeSkill.skill.name;
            String employeeName = employeeSkill.employee.name;
            List<String> employeeNames = employeesBySkill.get(skillName);
            if (employeeNames != null && !employeeNames.contains(employeeName)) {
                employeeNames.add(employeeName);
            }
        });

        return employeesBySkill;
    }

    /**
     * Validates and persists an employee-skill association.
     *
     * @param employeeSkill association data supplied by the caller
     * @return the persisted association, or {@code null} when an employee or skill
     *         is missing
     */
    @Transactional
    public EmployeeSkill create(EmployeeSkill employeeSkill) {
        Employee employee = repository.findEmployeeById(employeeSkill.employee.id);
        Skill skill = repository.findSkillById(employeeSkill.skill.id);
        if (employee == null || skill == null) {
            return null;
        }

        EmployeeSkill newEmployeeSkill = new EmployeeSkill();
        newEmployeeSkill.id = new EmployeeSkillId(employee.id, skill.id);
        newEmployeeSkill.employee = employee;
        newEmployeeSkill.skill = skill;
        newEmployeeSkill.proficiencyLevel = employeeSkill.proficiencyLevel;
        repository.persist(newEmployeeSkill);
        return newEmployeeSkill;
    }

    /**
     * Deletes an employee-skill association when it exists.
     *
     * @param employeeId employee identifier
     * @param skillId    skill identifier
     * @return {@code true} when an association was deleted
     */
    @Transactional
    public boolean delete(UUID employeeId, UUID skillId) {
        EmployeeSkill entity = repository.findById(new EmployeeSkillId(employeeId, skillId));
        if (entity == null) {
            return false;
        }

        repository.delete(entity);
        return true;
    }
}