package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dto.PaginatedResponse;
import org.acme.model.Skill;
import org.acme.repository.SkillRepository;

import java.util.List;
import java.util.UUID;

/** Business operations for skills and skill pagination. */
@ApplicationScoped
public class SkillService {

    /** Creates the skill service. */
    public SkillService() {
    }

    @Inject
    SkillRepository repository;

    /**
     * Lists skills on a page.
     *
     * @param page zero-based page index
     * @param pageSize maximum number of skills per page
     * @return skills with pagination metadata
     */
    public PaginatedResponse<Skill> listAll(int page, int pageSize) {
        long totalRecords = repository.countAll();
        return PaginatedResponse.of(repository.findPage(page, pageSize), totalRecords, page, pageSize);
    }

    /**
     * Finds a skill by identifier.
     *
     * @param id skill identifier
     * @return the skill, or {@code null} if none exists
     */
    public Skill getById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Persists a new skill and lets persistence generate its identifier.
     *
     * @param skill skill to create
     * @return the persisted skill
     */
    @Transactional
    public Skill create(Skill skill) {
        skill.id = null;
        repository.persist(skill);
        return skill;
    }

    /**
     * Persists multiple skills.
     *
     * @param skills skills to create
     */
    @Transactional
    public void createAll(List<Skill> skills) {
        repository.persist(skills);
    }

    /**
     * Updates the name and category of an existing skill.
     *
     * @param id skill identifier
     * @param updatedSkill replacement skill values
     * @return the updated skill, or {@code null} if none exists
     */
    @Transactional
    public Skill update(UUID id, Skill updatedSkill) {
        Skill skill = repository.findById(id);
        if (skill == null) {
            return null;
        }

        skill.name = updatedSkill.name;
        skill.category = updatedSkill.category;
        return skill;
    }

    /**
     * Deletes a skill by identifier.
     *
     * @param id skill identifier
     * @return {@code true} if a skill was deleted
     */
    @Transactional
    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
