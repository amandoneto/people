package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.EmployeeEvent;

import java.util.List;
import java.util.UUID;

/** Persistence repository for immutable employee events. */
@ApplicationScoped
public class EmployeeEventRepository implements PanacheRepositoryBase<EmployeeEvent, UUID> {

    /** Creates the employee event repository. */
    public EmployeeEventRepository() {
    }

    /**
     * Loads an employee's events in stream-version order.
     *
     * @param employeeId employee stream identifier
     * @return the events in ascending stream-version order
     */
    public List<EmployeeEvent> findStream(UUID employeeId) {
        return find("employeeId = ?1 order by streamVersion", employeeId).list();
    }
}