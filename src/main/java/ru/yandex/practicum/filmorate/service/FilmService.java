package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.DirectorStorage;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final FeedService feedService;

    @Autowired
    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage, DirectorStorage directorStorage, FeedService feedService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
        this.feedService = feedService;
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

    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public void delete(int filmId) {
        filmStorage.findById(filmId);
        filmStorage.delete(filmId);
    }

    public List<Film> searchFilms(String query, String by) {
        return filmStorage.searchFilms(query, by);
    }

    public void addDirectorToFilm(int filmId, int directorId) {
        filmStorage.addDirectorToFilm(filmId, directorId);
    }

    public List<Film> findFilmsByDirectorIdSortByYearAndTitle(int directorId, String sortBy) {
        if (sortBy.equalsIgnoreCase("likes")) {
            return filmStorage.findAllFilmsByDirectorIdSortByLikes(directorId);
        }
        if (sortBy.equalsIgnoreCase("year")) {
            return filmStorage.findAllFilmsByDirectorIdSortByYear(directorId);
        }
        throw new IllegalArgumentException("sortBy должен быть 'year' или 'likes'");
    }

    public List<Film> getMostPopularFilm(Integer year, Integer genreId, Integer count) {
        if (genreId == null && year == null) {
            return filmStorage.getPopularFilm(count);
        }
        return filmStorage.getMostPopularFilm(year, genreId, count);
    }

    public Film addRateFilm(int id, int userId, int rate) {
        if (rate < 1 || rate > 10) {
            throw new IncorrectParameterException("Оценка должна быть от 1 до 10");
        }
        filmStorage.findById(id);
        userStorage.findById(userId);
        return filmStorage.addRateFilm(id, userId, rate);

    }

    public void deleteRateFilm(int id, int userId) {
        filmStorage.findById(id);
        userStorage.findById(userId);
        filmStorage.deleteRateFilm(id, userId);
    }
}
