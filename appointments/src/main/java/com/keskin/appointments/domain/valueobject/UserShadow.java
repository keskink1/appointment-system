package com.keskin.appointments.domain.valueobject;

import java.util.UUID;

public record UserShadow(
        UUID id,
        String name,
        String email
) {

}
