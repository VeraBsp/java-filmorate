package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLikeFilm(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        return filmService.addLikeFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLikeFilm(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilm(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilm(count);
    }

}
