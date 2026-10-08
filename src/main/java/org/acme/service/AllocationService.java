package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.Allocation;
import org.acme.repository.AllocationRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AllocationService {

    @Inject
    AllocationRepository repository;

    public List<Allocation> listAll() {
        return repository.listAll();
    }

    public Allocation getById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public Allocation create(Allocation allocation) {
        allocation.id = null;
        repository.persist(allocation);
        return allocation;
    }

    @Transactional
    public Allocation update(UUID id, Allocation updatedAllocation) {
        Allocation allocation = repository.findById(id);
        if (allocation == null) {
            return null;
        }

        allocation.employee = updatedAllocation.employee;
        allocation.project = updatedAllocation.project;
        allocation.allocationPercentage = updatedAllocation.allocationPercentage;
        allocation.startDate = updatedAllocation.startDate;
        allocation.endDate = updatedAllocation.endDate;
        return allocation;
    }

    @Transactional
    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
