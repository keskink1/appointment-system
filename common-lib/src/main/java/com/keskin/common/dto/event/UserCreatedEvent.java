package com.keskin.common.dto.event;

import java.io.Serializable;
import java.util.UUID;

public record UserCreatedEvent(
        UUID uuid,
        String name,
        String email,
        long occurredAt // leave as long instead of localdatetime for serialization
) implements Serializable {
}
