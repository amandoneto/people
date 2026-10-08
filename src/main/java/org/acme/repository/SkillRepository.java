package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Skill;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SkillRepository implements PanacheRepositoryBase<Skill, UUID> {

    public List<Skill> findPage(int page, int pageSize) {
        return findAll().page(page, pageSize).list();
    }

    public long countAll() {
        return count();
    }

}
