package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    List<Film> getAll();

    Film update(Film film);

    Film findById(int filmId);

    void checkFieldsFilm(Film film);

    List<Film> getPopularFilm(int count);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    void delete(int filmId);

    List<Film> findAllFilmsByDirectorIdSortByLikes(int directorId);

    List<Film> findAllFilmsByDirectorIdSortByYear(int directorId);

    List<Film> searchFilms(String query, String by);

    void addDirectorToFilm(int filmId, int directorId);

    List<Film> getMostPopularFilm(Integer year, Integer genreId, Integer count);

    List<Film> getRecommendations(int id);

    Film addRateFilm(int id, int userId, int rate);

    void deleteRateFilm(int id, int userId);
}

