package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.model.Allocation;
import org.acme.repository.AllocationRepository;

import java.util.List;
import java.util.UUID;

/** Business operations for project allocations. */
@ApplicationScoped
public class AllocationService {

    /** Creates the allocation service. */
    public AllocationService() {
    }

    @Inject
    AllocationRepository repository;

    /**
     * Lists all persisted allocations.
     *
     * @return all allocations
     */
    public List<Allocation> listAll() {
        return repository.listAll();
    }

    /**
     * Finds an allocation by identifier.
     *
     * @param id allocation identifier
     * @return the allocation, or {@code null} if none exists
     */
    public Allocation getById(UUID id) {
        return repository.findById(id);
    }

    /**
     * Persists a new allocation and lets persistence generate its identifier.
     *
     * @param allocation allocation to create
     * @return the persisted allocation
     */
    @Transactional
    public Allocation create(Allocation allocation) {
        allocation.id = null;
        repository.persist(allocation);
        return allocation;
    }

    /**
     * Replaces the mutable values of an existing allocation.
     *
     * @param id allocation identifier
     * @param updatedAllocation replacement allocation values
     * @return the updated allocation, or {@code null} if it does not exist
     */
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

    /**
     * Deletes an allocation by identifier.
     *
     * @param id allocation identifier
     * @return {@code true} if an allocation was deleted
     */
    @Transactional
    public boolean delete(UUID id) {
        return repository.deleteById(id);
    }
}
