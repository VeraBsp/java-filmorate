package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class FilmReleaseDateIvalid extends RuntimeException {
    public FilmReleaseDateIvalid(String s) {
        super(s);
    }
}
