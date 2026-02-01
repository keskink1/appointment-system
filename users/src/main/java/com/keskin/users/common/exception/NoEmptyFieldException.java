package com.keskin.users.common.exception;

public class NoEmptyFieldException extends RuntimeException {
    public NoEmptyFieldException(Object fieldValue) {
        super(String.format("%s can't be empty", fieldValue));
    }
}
