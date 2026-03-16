package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.ReviewEntity;
import ru.yandex.practicum.filmorate.model.ReviewRequest;
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

    public ReviewEntity create(ReviewRequest dto) {
        userStorage.findById(dto.getUserId());
        filmStorage.findById(dto.getFilmId());
        ReviewEntity reviewEntity = new ReviewEntity(
                null,
                dto.getContent(),
                dto.getPositive(),
                dto.getUserId(),
                dto.getFilmId(),
                0
        );
        ReviewEntity createdReviewEntity = reviewStorage.create(reviewEntity);
        feedService.addEvent(
                createdReviewEntity.getUserId(),
                "REVIEW",
                "ADD",
                createdReviewEntity.getReviewId()
        );

        return createdReviewEntity;
    }

    public ReviewEntity findById(int id) {
        return reviewStorage.findById(id);
    }

    public ReviewEntity update(ReviewRequest dto) {
        if (dto.getReviewId() == null || dto.getReviewId() <= 0) {
            throw new IncorrectParameterException("Id отзыва указан некорректно");
        }
        ReviewEntity existingReviewEntity = reviewStorage.findById(dto.getReviewId());
        if (!existingReviewEntity.getUserId().equals(dto.getUserId())) {
            throw new IncorrectParameterException("Нельзя изменить пользователя, оставившего отзыв");
        }
        existingReviewEntity.setContent(dto.getContent());
        existingReviewEntity.setPositive(dto.getPositive());
        existingReviewEntity.setFilmId(dto.getFilmId());
        ReviewEntity updatedReviewEntity = reviewStorage.update(existingReviewEntity);
        feedService.addEvent(
                updatedReviewEntity.getUserId(),
                "REVIEW",
                "UPDATE",
                updatedReviewEntity.getReviewId()
        );
        return updatedReviewEntity;
    }

    public void delete(int id) {
        ReviewEntity reviewEntity = reviewStorage.findById(id);
        reviewStorage.delete(id);
        feedService.addEvent(
                reviewEntity.getUserId(),
                "REVIEW",
                "REMOVE",
                reviewEntity.getReviewId()
        );
    }

    public List<ReviewEntity> getAll(Integer filmId, Integer count) {
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
