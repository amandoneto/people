package org.acme.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Authentication result returned after a successful login.
 *
 * @param token signed JWT for subsequent authenticated requests
 * @param name employee name
 * @param seniority employee seniority level
 */
@RegisterForReflection
public record LoginResponse(String token, String name, String seniority) {
}