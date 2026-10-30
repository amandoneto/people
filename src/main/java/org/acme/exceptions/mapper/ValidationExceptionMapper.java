package org.acme.exceptions.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.stream.Collectors;

import org.acme.dto.ErrorResponse;

/** Maps bean-validation violations to bad-request API responses. */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /** Creates the validation-exception mapper. */
    public ValidationExceptionMapper() {
    }

    /**
     * Converts a validation exception to a response containing its violation messages.
     *
     * @param exception validation exception to map
     * @return bad-request response with the collected validation messages
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        // Junta todas as mensagens de erro de validação em uma única string (ou você
        // pode transformar em uma lista)
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.BAD_REQUEST.getStatusCode(),
                message);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}
