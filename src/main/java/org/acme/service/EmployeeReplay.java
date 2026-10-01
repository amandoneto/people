package org.acme.service;

import org.acme.model.EmployeeEventPayload;

public record EmployeeReplay(long version, EmployeeEventPayload state) {
}