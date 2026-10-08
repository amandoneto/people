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

    public static <T> PaginatedResponse<T> of(List<T> data, long totalRecords, int page, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        return new PaginatedResponse<>(
                data,
                totalRecords,
                page,
                pageSize,
                totalPages,
                page < totalPages - 1 ? page + 1 : null,
                page > 0 && totalPages > 0 ? page - 1 : null);
    }
}
