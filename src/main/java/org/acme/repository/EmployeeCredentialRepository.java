package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.EmployeeCredential;

import java.util.UUID;

/** Panache persistence repository for employee credentials keyed by employee UUID. */
@ApplicationScoped
public class EmployeeCredentialRepository implements PanacheRepositoryBase<EmployeeCredential, UUID> {

    /** Creates the employee credential repository. */
    public EmployeeCredentialRepository() {
    }
}