package com.keskin.common.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDto(
        int status,
        String message,
        LocalDateTime timestamp
) {}