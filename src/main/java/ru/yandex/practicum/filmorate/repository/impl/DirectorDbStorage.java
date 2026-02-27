package ru.yandex.practicum.filmorate.repository.impl;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.repository.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Component
public class DirectorDbStorage implements DirectorStorage {
    private final Logger log = LoggerFactory.getLogger(DirectorDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getAll() {
        String sql = "select * from directors";
        List<Director> directors = jdbcTemplate.query(sql, new RowMapper<Director>() {
            @Override
            public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Director(rs.getInt("director_id"),
                        rs.getString("director_name"));
            }
        });
        return directors;
    }

    @Override
    public Director findById(int directorId) {
        String sql = "select * from directors where director_id = ?";
        List<Director> directors = jdbcTemplate.query(sql, new RowMapper<Director>() {
            @Override
            public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Director(rs.getInt("director_id"),
                        rs.getString("director_name"));
            }
        }, directorId);
        if (directors.isEmpty()) {
            log.warn("Режиссер с id={} не найден", directorId);
            throw new ObjectNotFoundException("Режиссер с id=" + directorId + " не найден");
        }
        return directors.get(0);
    }

    @Override
    public Director create(Director director) {
        String sql = "insert into directors (director_name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        int directorId = keyHolder.getKey().intValue();
        director.setId(directorId);
        log.info("Добавлен новый режиссер: {} (id={})", director.getName(), directorId);
        return findById(directorId);
    }

    @Override
    @Transactional
    public Director update(Director director) {
        findById(director.getId());
        String sql = "update directors set director_name= ? WHERE director_id= ?";
        int rows = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (rows == 0) {
            throw new ObjectNotFoundException("Режиссер с id=" + director.getId() + " не найден");
        }
        return findById(director.getId());
    }

    @Override
    public void delete(int directorId) {
        findById(directorId);
        String sql = "delete from directors where director_id= ?";
        int rows = jdbcTemplate.update(sql, directorId);
        if (rows == 0) {
            throw new ObjectNotFoundException("Режиссер с id=" + directorId + " не найден");
        }
        log.info("Режиссер с id={} удален", directorId);
    }
}
