package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    List<Director> getAll();

    Director findById(int directorId);

    Director create(Director director);

    Director update(Director director);

    void delete(int directorId);
}
