package com.keskin.common.dto.event;

import java.util.UUID;

public record UserUpdatedEvent(
        UUID userId,
        String name,
        String email,
        long occurredAt // leave as long instead of localdatetime for serialization
) {
}
