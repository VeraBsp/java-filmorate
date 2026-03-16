package ru.yandex.practicum.filmorate.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.ReviewEntity;
import ru.yandex.practicum.filmorate.repository.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final Logger log = LoggerFactory.getLogger(ReviewDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ReviewEntity create(ReviewEntity reviewEntity) {
        String sql = """
                INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
                VALUES (?, ?, ?, ?, 0)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reviewEntity.getContent());
            ps.setBoolean(2, reviewEntity.getPositive());
            ps.setInt(3, reviewEntity.getUserId());
            ps.setInt(4, reviewEntity.getFilmId());
            return ps;
        }, keyHolder);
        reviewEntity.setReviewId(keyHolder.getKey().intValue());
        log.info("Создан новый отзыв: {} от пользователя с id={} к фильму с id={}", reviewEntity.getContent(), reviewEntity.getUserId(), reviewEntity.getFilmId());
        return reviewEntity;
    }

    @Override
    public ReviewEntity findById(int id) {
        List<ReviewEntity> reviewEntities = jdbcTemplate.query("select * from reviews where review_id = ?", reviewRowMapper(), id);
        if (reviewEntities.isEmpty()) {
            log.warn("Отзыв с id=" + id + " не найден");
            throw new ObjectNotFoundException("Отзыв с id=" + id + " не найден");
        }
        return reviewEntities.get(0);
    }

    @Override
    public ReviewEntity update(ReviewEntity reviewEntity) {
        findById(reviewEntity.getReviewId());
        String sql = """
                UPDATE reviews
                SET content = ?, is_positive = ?, film_id = ?
                WHERE review_id = ?
                """;
        jdbcTemplate.update(sql,
                reviewEntity.getContent(),
                reviewEntity.getPositive(),
                reviewEntity.getFilmId(),
                reviewEntity.getReviewId()
        );
        return findById(reviewEntity.getReviewId());
    }

    @Override
    public void delete(int id) {
        findById(id);
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Отзыв с id={} успешно удалён.", id);
    }

    @Override
    public List<ReviewEntity> getAll(Integer filmId, Integer count) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM reviews
                """);

        List<Object> params = new ArrayList<>();
        if (filmId != null) {
            sql.append(" WHERE film_id = ? ");
            params.add(filmId);
        }
        sql.append(" ORDER BY useful DESC LIMIT ? ");
        params.add(count);

        return jdbcTemplate.query(
                sql.toString(),
                reviewRowMapper(),
                params.toArray()
        );
    }

    @Override
    public void addLike(int id, int userId) {
        Integer oldReaction = getReaction(id, userId);
        if (oldReaction != null) {
            jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", id, userId);
            jdbcTemplate.update("UPDATE reviews SET useful = useful - ? WHERE review_id = ?", oldReaction, id);
        }
        jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id, reaction_type) VALUES (?, ?, 1)", id, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", id);
        log.info("Пользователь с id={} поставил лайк отзыву с id={}", userId, id);
    }

    @Override
    public void addDislike(int id, int userId) {
        Integer oldReaction = getReaction(id, userId);
        if (oldReaction != null) {
            jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", id, userId);
            jdbcTemplate.update("UPDATE reviews SET useful = useful - ? WHERE review_id = ?", oldReaction, id);
        }
        jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id, reaction_type) VALUES (?, ?, -1)", id, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", id);
        log.info("Пользователь с id={} поставил дизлайк отзыву с id={}", userId, id);
    }

    @Override
    public void deleteLike(int id, int userId) {
        int rows = jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND reaction_type = 1", id, userId);
        if (rows > 0) {
            jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", id);
        }
        log.info("Пользователь с id={} удалил лайк отзыву с id={}", userId, id);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        int rows = jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND reaction_type = -1", id, userId);
        if (rows > 0) {
            jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", id);
        }
        log.info("Пользователь с id={} удалил дизлайк отзыву с id={}", userId, id);
    }

    private Integer getReaction(int reviewId, int userId) {
        List<Integer> reactions = jdbcTemplate.query(
                "SELECT reaction_type FROM review_likes WHERE review_id = ? AND user_id = ?",
                (rs, rowNum) -> rs.getInt("reaction_type"),
                reviewId, userId
        );
        return reactions.isEmpty() ? null : reactions.get(0);
    }

    private RowMapper<ReviewEntity> reviewRowMapper() {
        return (rs, rowNum) -> new ReviewEntity(rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("useful")
        );
    }
}
