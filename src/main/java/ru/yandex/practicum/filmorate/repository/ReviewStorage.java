package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.ReviewEntity;

import java.util.List;

public interface ReviewStorage {
    ReviewEntity create(ReviewEntity reviewEntity);

    ReviewEntity findById(int id);

    ReviewEntity update(ReviewEntity reviewEntity);

    void delete(int id);

    List<ReviewEntity> getAll(Integer filmId, Integer count);

    void addLike(int id, int userId);

    void addDislike(int id, int userId);

    void deleteLike(int id, int userId);

    void deleteDislike(int id, int userId);
}

