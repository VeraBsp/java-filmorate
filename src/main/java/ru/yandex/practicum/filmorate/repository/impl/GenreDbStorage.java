package ru.yandex.practicum.filmorate.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDbStorage implements GenreStorage {
    private final Logger log = LoggerFactory.getLogger(GenreDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        List<Genre> genres = jdbcTemplate.query("select * from genres", new RowMapper<Genre>() {
            @Override
            public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Genre(rs.getInt("genre_id"),
                        rs.getString("genre_title"));
            }
        });
        return genres;
    }

    @Override
    public Genre findById(int genreId) {
        String sql = "SELECT genre_id, genre_title FROM genres WHERE genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new Genre(
                        rs.getInt("genre_id"),
                        rs.getString("genre_title")
                ),
                genreId
        );
        if (genres.isEmpty()) {
            log.warn("Жанр с id=" + genreId + " не найден");
            throw new ObjectNotFoundException("Жанр с id=" + genreId + " не найден");
        }
        return genres.get(0);
    }
}
