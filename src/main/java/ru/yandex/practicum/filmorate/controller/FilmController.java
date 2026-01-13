package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int nextId = 1;
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate RELEASE_DATE =
            LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        film.setId(nextId++);
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {

        if (film.getId() <= 0) {
            throw new ValidationException("Id фильма должен быть указан");
        }

        Film existingFilm = films.get(film.getId());
        if (existingFilm == null) {
            throw new ValidationException("Фильм с id=" + film.getId() + " не найден");
        }

        validateFilm(film);

        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        //films.put(existingFilm.getId(), existingFilm);
        return existingFilm;
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
