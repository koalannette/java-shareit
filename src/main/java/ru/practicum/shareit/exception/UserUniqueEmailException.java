package ru.practicum.shareit.exception;

public class UserUniqueEmailException extends RuntimeException {
    public UserUniqueEmailException(String message) {
        super(message);
    }
}