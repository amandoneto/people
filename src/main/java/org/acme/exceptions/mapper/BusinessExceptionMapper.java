package org.acme.exceptions.mapper;

import org.acme.dto.ErrorResponse;
import org.acme.exceptions.BusinessException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// A anotação @Provider registra o mapper automaticamente no contexto do Quarkus
/** Maps business exceptions to bad-request API responses. */
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    /** Creates the business-exception mapper. */
    public BusinessExceptionMapper() {
    }

    /**
     * Converts a business exception to a bad-request response.
     *
     * @param exception business exception to map
     * @return response containing the exception message
     */
    @Override
    public Response toResponse(BusinessException exception) {
        // Criando um objeto de resposta de erro padronizado
        ErrorResponse errorResponse = new ErrorResponse(
                Response.Status.BAD_REQUEST.getStatusCode(),
                exception.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }
}
