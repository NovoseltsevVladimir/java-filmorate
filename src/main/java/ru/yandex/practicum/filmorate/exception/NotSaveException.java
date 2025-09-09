package ru.yandex.practicum.filmorate.exception;

public class NotSaveException extends RuntimeException {
    public NotSaveException(String message) {
        super(message);
    }
}