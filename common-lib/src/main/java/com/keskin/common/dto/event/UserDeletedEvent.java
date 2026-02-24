package com.keskin.common.dto.event;

import java.io.Serializable;
import java.util.UUID;

public record UserDeletedEvent(
        UUID userId,
        long occurredAt
) implements Serializable {
}
