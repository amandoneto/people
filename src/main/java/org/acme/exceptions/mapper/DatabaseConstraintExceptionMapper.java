package org.acme.exceptions.mapper;

import org.acme.dto.ErrorResponse;
import org.hibernate.exception.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/** Maps database constraint violations to conflict API responses. */
@Provider
public class DatabaseConstraintExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /** Creates the database-constraint mapper. */
    public DatabaseConstraintExceptionMapper() {
    }

    /**
     * Converts a database constraint violation to a conflict response.
     *
     * @param exception database constraint violation
     * @return conflict response with a generic or duplicate-key message
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = "Database constraint violation.";

        // Refining the message if it's a duplicate key violation
        if (exception.getMessage() != null && exception.getMessage().contains("duplicate key")) {
            message = "A record with these details already exists.";
        }

        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.CONFLICT.getStatusCode(),
                message);

        return Response.status(Response.Status.CONFLICT)
                .entity(errorResponse)
                .build();
    }
}
