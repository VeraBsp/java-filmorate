package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.RatingStorage;

import java.util.List;

@Service
public class RatingService {
    private final RatingStorage ratingStorage;

@Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> getAllRatings() {
        return ratingStorage.getAllRatings();
    }

    public Rating findRatingById(int ratingId) {
        return ratingStorage.findRatingById(ratingId);
    }
}
