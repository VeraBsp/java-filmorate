package ru.yandex.practicum.filmorate.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.repository.FeedStorage;

import java.util.List;

@Component
public class FeedDbStorage implements FeedStorage {
    private final Logger log = LoggerFactory.getLogger(ReviewDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getFeed(int id) {
        String sql = "select * from event_feed where user_id = ?";
        List<Feed> feeds = jdbcTemplate.query(sql, (rs, rowNum) -> new Feed(
                rs.getInt("event_id"),
                rs.getString("event_type"),
                rs.getString("operation"),
                rs.getLong("event_timestamp"),
                rs.getInt("user_id"),
                rs.getInt("entity_id")
                ), id);
        return feeds;
    }

    @Override
    public void addEvent(int userId, String eventType, String operation, int entityId) {
        String sql = """
        INSERT INTO event_feed (event_type, operation, event_timestamp, user_id, entity_id)
        VALUES (?, ?, ?, ?, ?)
        """;

        jdbcTemplate.update(sql,
                eventType,
                operation,
                System.currentTimeMillis(),
                userId,
                entityId);
    }


}
