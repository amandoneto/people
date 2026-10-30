package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Skill;

import java.util.List;
import java.util.UUID;

/** Persistence queries for skills and skill pages. */
@ApplicationScoped
public class SkillRepository implements PanacheRepositoryBase<Skill, UUID> {

    /** Creates the skill repository. */
    public SkillRepository() {
    }

    /**
     * Loads one page of skills.
     *
     * @param page zero-based page index
     * @param pageSize maximum number of skills to return
     * @return skills on the requested page
     */
    public List<Skill> findPage(int page, int pageSize) {
        return findAll().page(page, pageSize).list();
    }

    /**
     * Counts all persisted skills.
     *
     * @return total number of skills
     */
    public long countAll() {
        return count();
    }

}
