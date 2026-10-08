package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.acme.model.Employee;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EmployeeRepository implements PanacheRepositoryBase<Employee, UUID> {

    @Inject
    EntityManager entityManager;

    public Employee findActiveById(UUID employeeId) {
        return find("id = ?1 and deletedAt is null", employeeId).firstResult();
    }

    public Employee findActiveByEmail(String email) {
        return find("email = ?1 and deletedAt is null", email).firstResult();
    }

    public List<Employee> findActivePage(int page, int pageSize) {
        return find("deletedAt is null").page(page, pageSize).list();
    }

    public long countActive() {
        return count("deletedAt is null");
    }

    public Employee findByIdForUpdate(UUID employeeId) {
        return entityManager.find(Employee.class, employeeId, LockModeType.PESSIMISTIC_WRITE);
    }

    public void flush() {
        entityManager.flush();
    }
}
