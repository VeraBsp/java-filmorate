package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Collection<Film> findAll();

    Film updateFilm(Film film);

    Film findFilmById(int filmId);

    Film addLikeFilm(int filmId, int userId);

    void checkFieldsFilm(Film film);

    void deleteLikeFilm(Integer id, Integer userId);

    List<Film> getPopularFilm(int count);
}
