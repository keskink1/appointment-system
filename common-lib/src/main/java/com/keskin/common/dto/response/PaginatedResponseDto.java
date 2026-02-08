package com.keskin.common.dto.response;

import java.util.List;

public record PaginatedResponseDto<T>(
        List<T> data,
        long totalElements,
        int totalPages,
        int currentPage
) {
}
