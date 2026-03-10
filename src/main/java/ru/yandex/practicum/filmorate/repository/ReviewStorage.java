package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(@Valid Review review);

    Review findById(int id);

    Review update(@Valid Review review);

    void delete(int id);

    List<Review> getAll(Integer filmId, Integer count);

    void addLike(int id, int userId);

    void addDislike(int id, int userId);

    void deleteLike(int id, int userId);

    void deleteDislike(int id, int userId);
}

