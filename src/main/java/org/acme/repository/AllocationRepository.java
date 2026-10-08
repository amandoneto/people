package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.Allocation;

import java.util.UUID;

@ApplicationScoped
public class AllocationRepository implements PanacheRepositoryBase<Allocation, UUID> {
}
