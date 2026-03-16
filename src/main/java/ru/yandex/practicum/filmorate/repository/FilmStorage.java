package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    List<Film> getAll();

    Film update(Film film);

    Film findById(int filmId);

    Film addLikeFilm(int filmId, int userId);

    void checkFieldsFilm(Film film);

    void deleteLikeFilm(Integer id, Integer userId);

    List<Film> getPopularFilm(int count);

    List<Film> getCommonFilms(Integer userId, Integer friendId);

    void delete(int filmId);

    List<Film> findAllFilmsByDirectorIdSortByLikes(int directorId);

    List<Film> findAllFilmsByDirectorIdSortByYear(int directorId);

    List<Film> searchFilms(String query, String by);

    void addDirectorToFilm(int filmId, int directorId);

    List<Film> getMostPopularFilm(Integer year, Integer genreId, Integer count);

    List<Film> getRecommendations(int id);
}

