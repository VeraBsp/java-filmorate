package ru.yandex.practicum.filmorate.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class RatingDbStorage implements RatingStorage {
    private final Logger log = LoggerFactory.getLogger(RatingDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getAllRatings() {
        List<Rating> ratings = jdbcTemplate.query("select * from rating", new RowMapper<Rating>() {
            @Override
            public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Rating(rs.getInt("rating_id"),
                        rs.getString("rating_title"));
            }
        });
        return ratings;
    }

    @Override
    public Rating findRatingById(int ratingId) {
        String sql = "select rating_id, rating_title from rating where rating_id = ?";
        List<Rating> ratings = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Rating(
                        rs.getInt("rating_id"),
                        rs.getString("rating_title")
                ),
                ratingId
        );
        if (ratings.isEmpty()) {
            log.warn("Рейтинг с id=" + ratingId + " не найден");
            throw new ObjectNotFoundException("Рейтинг с id=" + ratingId + " не найден");
        }
        return ratings.get(0);
    }
}
