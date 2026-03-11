package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FilmStorage filmStorage, UserStorage userStorage) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Review create(Review review) {
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());
        return reviewStorage.create(review);
    }

    public Review findById(int id) {
        return reviewStorage.findById(id);
    }

    public Review update(Review review) {
        return reviewStorage.update(review);
    }

    public void delete(int id) {
        reviewStorage.delete(id);
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        if (filmId != null) {
            filmStorage.findById(filmId);
        }
        return reviewStorage.getAll(filmId, count);
    }

    public void addLike(int id, int userId) {
        findById(id);
        userStorage.findById(userId);
        reviewStorage.addLike(id, userId);
    }

    public void addDislike(int id, int userId) {
        findById(id);
        userStorage.findById(userId);
        reviewStorage.addDislike(id, userId);
    }

    public void deleteLike(int id, int userId) {
        userStorage.findById(userId);
        findById(id);
        reviewStorage.deleteLike(id, userId);
    }

    public void deleteDislike(int id, int userId) {
        userStorage.findById(userId);
        findById(id);
        reviewStorage.deleteDislike(id, userId);
    }
}
