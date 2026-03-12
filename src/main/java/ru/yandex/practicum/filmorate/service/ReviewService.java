package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.ReviewDto;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.ReviewStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedService feedService;

    @Autowired
    public ReviewService(ReviewStorage reviewStorage, FilmStorage filmStorage, UserStorage userStorage, FeedService feedService) {
        this.reviewStorage = reviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.feedService = feedService;
    }

    public Review create(ReviewDto dto) {
        userStorage.findById(dto.getUserId());
        filmStorage.findById(dto.getFilmId());
        Review review = new Review(
                null,
                dto.getContent(),
                dto.getPositive(),
                dto.getUserId(),
                dto.getFilmId(),
                0
        );
        Review createdReview = reviewStorage.create(review);
        feedService.addEvent(
                createdReview.getUserId(),
                "REVIEW",
                "ADD",
                createdReview.getReviewId()
        );

        return createdReview;
    }

    public Review findById(int id) {
        return reviewStorage.findById(id);
    }

    public Review update(ReviewDto dto) {
        if (dto.getReviewId() == null || dto.getReviewId() <= 0) {
            throw new IncorrectParameterException("Id отзыва указан некорректно");
        }
        Review existingReview = reviewStorage.findById(dto.getReviewId());
        if (!existingReview.getUserId().equals(dto.getUserId())) {
            throw new IncorrectParameterException("Нельзя изменить пользователя, оставившего отзыв");
        }
        existingReview.setContent(dto.getContent());
        existingReview.setPositive(dto.getPositive());
        existingReview.setFilmId(dto.getFilmId());
        Review updatedReview = reviewStorage.update(existingReview);
        feedService.addEvent(
                updatedReview.getUserId(),
                "REVIEW",
                "UPDATE",
                updatedReview.getReviewId()
        );
        return updatedReview;
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
        feedService.addEvent(userId, "LIKE", "ADD", id);
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
        feedService.addEvent(userId, "LIKE", "REMOVE", id);
    }

    public void deleteDislike(int id, int userId) {
        userStorage.findById(userId);
        findById(id);
        reviewStorage.deleteDislike(id, userId);
    }
}
