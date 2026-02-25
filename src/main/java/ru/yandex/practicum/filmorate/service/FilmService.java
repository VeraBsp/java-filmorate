package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film findById(int id) {
        return filmStorage.findById(id);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film addLikeFilm(int filmId, int userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        return filmStorage.addLikeFilm(filmId, userId);
    }

    public void deleteLikeFilm(int id, int userId) {
        filmStorage.findById(id);
        userStorage.findById(userId);
        filmStorage.deleteLikeFilm(id, userId);
    }

    public List<Film> getPopularFilm(int count) {
        return filmStorage.getPopularFilm(count);
    }
}
