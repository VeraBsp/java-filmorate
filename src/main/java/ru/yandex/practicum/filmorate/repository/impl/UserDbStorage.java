package ru.yandex.practicum.filmorate.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        validateEmailFormat(user);
        validateEmailUniqueness(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        String sql = """
                INSERT INTO users (name, email, login, birthday)
                VALUES (?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getLogin());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        log.info("Создан новый пользователь: {} (id={})", user.getName(), user.getId());
        return user;
    }

    @Override
    public void delete(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId);
        if (rowsAffected == 0) {
            log.warn("Пользователь с id={} не найден для удаления.", userId);
            throw new IncorrectParameterException("Пользователь с указанным id не найден.");
        }
        log.info("Пользователь с id={} успешно удалён.", userId);
    }

    @Override
    public User update(User user) {
        validateEmailFormat(user);
        if (user.getId() <= 0) {
            throw new IncorrectParameterException("Id пользователя указан некорректно");
        }
        validateEmailUniquenessForUpdate(user);
        String sql = """
                UPDATE users
                SET name = ?, login = ?, email = ?, birthday = ?
                WHERE user_id = ?
                """;
        int rows = jdbcTemplate.update(sql,
                user.getName() == null || user.getName().isBlank()
                        ? user.getLogin()
                        : user.getName(),
                user.getLogin(),
                user.getEmail(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        if (rows == 0) {
            throw new ObjectNotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        return findById(user.getId());
    }

    @Override
    public User findById(int userId) {
        List<User> users = jdbcTemplate.query("select * from users where user_id = ?", userRowMapper(), userId);
        if (users.isEmpty()) {
            log.warn("Пользователь с id=" + userId + " не найден");
            throw new ObjectNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return users.get(0);
    }

    @Override
    public void addFriends(Integer userId, Integer friendId) {
        findById(userId);
        findById(friendId);
        if (userId.equals(friendId)) {
            log.warn("Нельзя добавить себя в друзья.");
            throw new IncorrectParameterException("Нельзя добавить себя в друзья.");
        }
        String checkSql = "SELECT COUNT(*) FROM friends WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, userId, friendId);

        if (count > 0) {
            log.info("Пользователь с id={} уже является другом пользователя id={}", friendId, userId);
            throw new IncorrectParameterException("Пользователи уже друзья.");
            //    return;
        }
        String sql = "INSERT INTO friends (user_id, friend_id, friend_status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, 2);
        log.info("Пользователь с id={} добавлен в друзья к пользователю id={}", friendId, userId);
    }

    @Override
    public void deleteFriends(Integer userId, Integer friendId) {
        findById(userId);
        findById(friendId);
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
        log.info("Пользователь с id={} удалён из друзей пользователя id={}", friendId, userId);
    }


    @Override
    public List<User> getFriendsThisUser(Integer userId) {
        findById(userId);
        String sql = """
                SELECT uf.user_id,
                       uf.name,
                       uf.email,
                       uf.login,
                       uf.birthday
                FROM friends f
                JOIN users uf ON f.friend_id = uf.user_id
                WHERE f.user_id = ?
                  AND f.friend_status_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                userRowMapper(),
                userId,
                2
        );
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        findById(userId);
        findById(otherId);
        String sql = """
                SELECT u.user_id,
                       u.name,
                       u.email,
                       u.login,
                       u.birthday
                FROM friends f1
                JOIN friends f2
                     ON f1.friend_id = f2.friend_id
                JOIN users u
                     ON u.user_id = f1.friend_id
                WHERE f1.user_id = ?
                  AND f2.user_id = ?
                  AND f1.friend_status_id = 2
                  AND f2.friend_status_id = 2
                """;
        List<User> commonFriends = jdbcTemplate.query(
                sql,
                userRowMapper(),
                userId,
                otherId
        );

        if (commonFriends.isEmpty()) {
            log.debug("Общие друзья для пользователей id={} и id={} не найдены", userId, otherId);
        } else {
            log.debug("Найдено {} общих друзей для пользователей id={} и id={}",
                    commonFriends.size(), userId, otherId);
        }
        return commonFriends;
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("select * from users", userRowMapper());
    }

    @Override
    public void validateEmailFormat(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Некорректный адрес электронной почты.");
            throw new IncorrectParameterException("Некорректный адрес электронной почты.");
        }
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                LocalDate.parse(rs.getString("birthday"))
        );
    }

    private void validateEmailUniqueness(User user) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, user.getEmail());

        if (count != null && count > 0) {
            log.info("Пользователь с электронной почтой уже зарегистрирован.");
            throw new IncorrectParameterException(
                    String.format("Пользователь с электронной почтой %s уже зарегистрирован.", user.getEmail())
            );
        }
    }

    private void validateEmailUniquenessForUpdate(User user) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND user_id <> ?";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, user.getEmail(), user.getId());

        if (count != null && count > 0) {
            log.info("Пользователь с электронной почтой уже зарегистрирован.");
            throw new IncorrectParameterException(
                    String.format("Пользователь с электронной почтой %s уже зарегистрирован.", user.getEmail())
            );
        }
    }
}
