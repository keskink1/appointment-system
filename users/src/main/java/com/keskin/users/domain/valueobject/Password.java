package com.keskin.users.domain.valueobject;

import com.keskin.users.common.exception.InvalidValidationException;

public record Password(
        String value
) {
    public Password {
        if (value == null || value.length() < 4) {
            throw new InvalidValidationException("Password must be at least 4 characters long!");
        }
    }
}
