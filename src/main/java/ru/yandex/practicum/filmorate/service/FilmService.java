package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {
    FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film addLikeFilm(int filmId, int userId) {
        return filmStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLikeFilm(int id, int userId) {
        filmStorage.deleteLikeFilm(id, userId);
    }

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getPopularFilm(count);
    }
}
