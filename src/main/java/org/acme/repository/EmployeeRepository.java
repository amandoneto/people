package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.acme.model.Employee;

import java.util.List;
import java.util.UUID;

/** Persistence queries for employee profiles and active employee projections. */
@ApplicationScoped
public class EmployeeRepository implements PanacheRepositoryBase<Employee, UUID> {

    /** Creates the employee repository. */
    public EmployeeRepository() {
    }

    @Inject
    EntityManager entityManager;

    /**
     * Finds an employee by identifier, excluding soft-deleted profiles.
     *
     * @param employeeId employee identifier
     * @return active employee, or {@code null} if none exists
     */
    public Employee findActiveById(UUID employeeId) {
        return find("id = ?1 and deletedAt is null", employeeId).firstResult();
    }

    /**
     * Finds an active employee by email address.
     *
     * @param email email address to match
     * @return active employee, or {@code null} if none exists
     */
    public Employee findActiveByEmail(String email) {
        return find("email = ?1 and deletedAt is null", email).firstResult();
    }

    /**
     * Loads one page of active employees.
     *
     * @param page zero-based page index
     * @param pageSize maximum number of employees to return
     * @return employees on the requested page
     */
    public List<Employee> findActivePage(int page, int pageSize) {
        return find("deletedAt is null").page(page, pageSize).list();
    }

    /**
     * Counts employees that have not been soft-deleted.
     *
     * @return number of active employees
     */
    public long countActive() {
        return count("deletedAt is null");
    }

    /**
     * Finds an employee while acquiring a pessimistic write lock.
     *
     * @param employeeId employee identifier
     * @return employee with the specified identifier, or {@code null} if absent
     */
    public Employee findByIdForUpdate(UUID employeeId) {
        return entityManager.find(Employee.class, employeeId, LockModeType.PESSIMISTIC_WRITE);
    }

    /** Flushes pending persistence changes to the database. */
    public void flush() {
        entityManager.flush();
    }
}
