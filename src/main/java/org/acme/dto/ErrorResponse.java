package org.acme.dto;

import java.time.LocalDateTime;

/**
 * Error details returned by the API.
 *
 * @param status HTTP status code
 * @param message description of the error
 * @param timestamp time at which this response object was created
 */
public record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp) {

    /**
     * Creates an error response timestamped at the current local time.
     *
     * @param status HTTP status code
     * @param message description of the error
     */
    public ErrorResponse(int status, String message) {
        this(status, message, LocalDateTime.now());
    }
}
