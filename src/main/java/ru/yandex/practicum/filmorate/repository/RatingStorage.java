package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingStorage {
    List<Rating> getAll();

    Rating findById(int ratingId);
}
