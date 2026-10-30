package org.acme.exceptions;

/** An unchecked exception representing a business-rule failure. */
public class BusinessException extends RuntimeException {
    /**
     * Creates an exception with a detail message.
     *
     * @param message detail message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a detail message and cause.
     *
     * @param message detail message
     * @param cause underlying cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
