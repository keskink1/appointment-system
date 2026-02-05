package com.keskin.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequestDto(
        @NotBlank
        String name,
        @Min(0)
        Integer age,
        @Email
        String email,
        @NotBlank
        String password
) {
}
