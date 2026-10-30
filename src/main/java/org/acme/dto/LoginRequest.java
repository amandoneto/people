package org.acme.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credentials submitted to the login endpoint.
 *
 * @param email employee email address
 * @param password plaintext password to verify
 */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password) {
}