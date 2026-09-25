package org.acme.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> data,
        long totalRecords,
        int page,
        int pageSize,
        int totalPages,
        Integer nextPage,
        Integer previousPage) {
}
