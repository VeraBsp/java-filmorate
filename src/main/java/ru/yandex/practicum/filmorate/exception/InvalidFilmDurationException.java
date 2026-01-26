package ru.yandex.practicum.filmorate.exception;

public class InvalidFilmDurationException extends RuntimeException {
    public InvalidFilmDurationException(String message) {
        super(message);
    }
}
