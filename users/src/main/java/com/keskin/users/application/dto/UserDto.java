package com.keskin.users.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keskin.users.domain.model.Role;

import java.util.UUID;

public record UserDto(
        @JsonIgnore
        UUID id,
        String name,
        Integer age,
        String email,
        Role role,
        Boolean active
) {
}
