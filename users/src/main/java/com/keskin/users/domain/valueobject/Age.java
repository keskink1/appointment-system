package com.keskin.users.domain.valueobject;

import com.keskin.common.exception.NoEmptyFieldException;
import jakarta.validation.ValidationException;

public record Age(
        Integer value
) {
    public Age {
        if (value == null) throw new NoEmptyFieldException("Age");
        if (value < 0) throw new ValidationException("Age can't be smaller than 0");
    }
}
