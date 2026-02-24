package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;
    private static final Logger log = LoggerFactory.getLogger(RatingController.class);

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public Collection<Rating> getAll() {
        log.info("Сформирован запрос на получение всех рейтингов");
        return ratingService.getAll();
    }

    @GetMapping("{id}")
    public Rating findById(@PathVariable("id") int ratingId) {
        log.info("Получен запрос на получение рейтинга с id={}", ratingId);
        return ratingService.findById(ratingId);
    }
}
