package com.keskin.common.dto.event;

import java.util.UUID;

public record UserDeletedEvent(
        UUID userId,
        long occuredAt
) {
}
