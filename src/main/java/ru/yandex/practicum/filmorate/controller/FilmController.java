package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final List<Film> films = new ArrayList<>();
    private static final LocalDate RELEASE_DATE =
            LocalDate.of(1895, 12, 28);

    @GetMapping
    public List<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validateFilm(film);
        films.add(film);
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        validateFilm(film);
        films.add(film);

        return film;
    }

    private void validateFilm(Film film){
        if(film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if(film.getDescription().length()>200) {
            throw new ValidationException("Максимальная длина описания фильма должна быть не больше 200 символов");
        }
        if(film.getDuration()<0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if(film.getReleaseDate().isBefore(RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
