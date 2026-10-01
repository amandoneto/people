package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.model.EmployeeEvent;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EmployeeEventRepository implements PanacheRepositoryBase<EmployeeEvent, UUID> {

    public List<EmployeeEvent> findStream(UUID employeeId) {
        return find("employeeId = ?1 order by streamVersion", employeeId).list();
    }
}