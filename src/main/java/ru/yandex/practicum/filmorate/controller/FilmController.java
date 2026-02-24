package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Сформирован запрос на получение всех фильмов");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        log.info("Сформирован запрос на получение фильма с id={}", id);
        return filmService.findById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма");
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма film={}", film);
        return filmService.update(film);
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Получен запрос на добавление лайка фильму с id={}  от пользователя с userId={}",id, userId);
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        log.info("Получен запрос на удаление лайка фильму с id={}  от пользователя с userId={}",id, userId);
        filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@Positive @RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен запрос на выборку count={} популярных фильмов", count);
        return filmService.getPopularFilm(count);
    }

}
