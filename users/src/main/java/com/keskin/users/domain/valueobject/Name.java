package com.keskin.users.domain.valueobject;

import com.keskin.common.exception.NoEmptyFieldException;
import jakarta.validation.ValidationException;

public record Name(
        String value
) {

    public Name {
        if (value == null || value.isBlank()) {
            throw new NoEmptyFieldException("Name");
        }

        if (value.trim().length() < 2) {
            throw new ValidationException("Name is too short!");
        }

        value = value.trim();
    }
}
