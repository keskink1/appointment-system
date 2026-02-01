package com.keskin.users.application.dto;

public record UpdateUserRequestDto(
        String name,
        Integer age,
        String email
) {
}
