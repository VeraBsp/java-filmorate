package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        log.info("Получен запрос на создание отзыва");
        return reviewService.create(review);
    }

    @GetMapping("{id}")
    public Review findById(@PathVariable("id") int id) {
        log.info("Сформирован запрос на получение отзыва с id={}", id);
        return reviewService.findById(id);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Получен запрос на обновление отзыва id={}", review.getReviewId());
        return reviewService.update(review);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") int id) {
        log.info("Получен запрос на удаление отзыва с id={}", id);
        reviewService.delete(id);
    }

    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") Integer count) {

        return reviewService.getAll(filmId, count);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.deleteDislike(id, userId);
    }
}
