package org.acme.exceptions.mapper;

import org.acme.dto.ErrorResponse;
import org.acme.exceptions.BusinessException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// A anotação @Provider registra o mapper automaticamente no contexto do Quarkus
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

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
