package org.acme.dto;

import java.util.List;

/**
 * A page of results with record counts and navigation metadata.
 *
 * @param <T> result item type
 * @param data records on the current page
 * @param totalRecords number of matching records
 * @param page zero-based current page index
 * @param pageSize maximum number of records per page
 * @param totalPages number of pages calculated from the record count and page size
 * @param nextPage next page index, or {@code null} if there is no next page
 * @param previousPage previous page index, or {@code null} if there is no previous page
 */
public record PaginatedResponse<T>(
        List<T> data,
        long totalRecords,
        int page,
        int pageSize,
        int totalPages,
        Integer nextPage,
        Integer previousPage) {

    /**
     * Creates pagination metadata for the supplied page.
     *
     * @param <T> result item type
     * @param data records on the current page
     * @param totalRecords number of matching records
     * @param page zero-based current page index
     * @param pageSize maximum number of records per page
     * @return the page and its navigation metadata
     */
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
