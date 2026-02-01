package com.keskin.users.domain.valueobject;

import com.keskin.users.common.exception.InvalidValidationException;
import com.keskin.users.common.exception.NoEmptyFieldException;

import java.util.regex.Pattern;

public record Email(
        String value
) {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public Email {
        if (value == null || value.isBlank()){
            throw new NoEmptyFieldException("Email");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidValidationException("Invalid email format: " + value);
        }

        value = value.toLowerCase().trim();
    }
}
