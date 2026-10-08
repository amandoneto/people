package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.dto.PaginatedResponse;
import org.acme.model.Skill;
import org.acme.repository.SkillRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class SkillService {

    @Inject
    SkillRepository repository;

    public PaginatedResponse<Skill> listAll(int page, int pageSize) {
        long totalRecords = repository.countAll();
        return PaginatedResponse.of(repository.findPage(page, pageSize), totalRecords, page, pageSize);
    }

    public Skill getById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public Skill create(Skill skill) {
        skill.id = null;
        repository.persist(skill);
        return skill;
    }

    @Transactional
    public void createAll(List<Skill> skills) {
        repository.persist(skills);
    }

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

    @Transactional
    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
