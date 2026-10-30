package org.acme.service;

import org.acme.model.EmployeeEventPayload;

/**
 * Reconstructed employee state and the stream version from which it was replayed.
 *
 * @param version final event stream version
 * @param state employee state represented by that event
 */
public record EmployeeReplay(long version, EmployeeEventPayload state) {
}