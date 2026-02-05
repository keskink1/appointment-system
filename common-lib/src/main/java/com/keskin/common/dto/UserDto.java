package com.keskin.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keskin.common.enums.Role;

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
