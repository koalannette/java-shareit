package ru.practicum.shareit.exception;

public class UserExistsAlreadyException extends RuntimeException {
    public UserExistsAlreadyException(String message) {
        super(message);
    }
}

