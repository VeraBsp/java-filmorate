package ru.yandex.practicum.filmorate.repository.impl;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.*;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Film create(Film film) {
        checkFieldsFilm(film);
        validateRatingExists(film.getMpa().getId());
        String sql = """
                INSERT INTO films (film_name, description, duration, release_date, rating_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setDate(4, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        saveFilmGenres(filmId, film.getGenres());
        saveFilmDirector(filmId, film.getDirectors());
        log.info("Создан новый фильм: {} (id={})", film.getName(), filmId);
        return findById(filmId);
    }

    @Override
    public List<Film> getAll() {
        String sql = """
                SELECT f.film_id,
                       f.film_name,
                       f.description,
                       f.release_date,
                       f.duration,
                       r.rating_id,
                       r.rating_title,
                       g.genre_id,
                       g.genre_title,
                       d.director_id,
                       d.director_name
                FROM films f
                LEFT JOIN rating r ON f.rating_id = r.rating_id
                LEFT JOIN film_genre fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                LEFT JOIN film_director fd ON f.film_id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.director_id
                ORDER BY f.film_id
                """;

        return jdbcTemplate.query(sql, rs -> {

            Map<Integer, Film> filmMap = new LinkedHashMap<>();

            while (rs.next()) {

                int filmId = rs.getInt("film_id");

                Film film = filmMap.get(filmId);
                if (film == null) {
                    film = new Film();
                    film.setId(filmId);
                    film.setName(rs.getString("film_name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));
                    Rating rating = new Rating(
                            rs.getInt("rating_id"),
                            rs.getString("rating_title")
                    );
                    film.setMpa(rating);
                    film.setGenres(new HashSet<>());
                    film.setDirectors(new HashSet<>());
                    filmMap.put(filmId, film);
                }
                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre(
                            genreId,
                            rs.getString("genre_title")
                    );
                    film.getGenres().add(genre);
                }

                int directorId = rs.getInt("director_id");
                if (!rs.wasNull()) {
                    Director director = new Director(
                            directorId,
                            rs.getString("director_name")
                    );
                    film.getDirectors().add(director);
                }
            }
            return new ArrayList<>(filmMap.values());
        });
    }

    @Override
    @Transactional
    public Film update(Film film) {
        if (film.getId() <= 0) {
            throw new IncorrectParameterException("Id фильма указан некорректно");
        }
        findById(film.getId());
        validateRatingExists(film.getMpa().getId());
        String sql = "UPDATE films SET film_name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE film_id = ? ";
        int rows = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        if (rows == 0) {
            throw new ObjectNotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        updateFilmGenres(film.getId(), film.getGenres());
        updateFilmDirectors(film.getId(), film.getDirectors());
        return findById(film.getId());
    }

    @Override
    public Film findById(int filmId) {
        String sql = """
                SELECT f.film_id,
                       f.film_name,
                       f.description,
                       f.release_date,
                       f.duration,
                       r.rating_id,
                       r.rating_title,
                       g.genre_id,
                       g.genre_title,
                       d.director_id,
                       d.director_name
                FROM films f
                LEFT JOIN rating r ON f.rating_id = r.rating_id
                LEFT JOIN film_genre fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                LEFT JOIN film_director fd ON f.film_id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.director_id
                WHERE f.film_id = ?
                ORDER BY g.genre_id
                """;
        return jdbcTemplate.query(sql, rs -> {

            Film film = null;

            while (rs.next()) {

                if (film == null) {

                    film = new Film();
                    film.setId(rs.getInt("film_id"));
                    film.setName(rs.getString("film_name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));
                    int ratingId = rs.getInt("rating_id");
                    if (!rs.wasNull()) {
                        Rating rating = new Rating(
                                ratingId,
                                rs.getString("rating_title")
                        );
                        film.setMpa(rating);
                    }

                    film.setGenres(new LinkedHashSet<>());
                    film.setDirectors(new HashSet<>());
                }
                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre(
                            genreId,
                            rs.getString("genre_title")
                    );
                    film.getGenres().add(genre);
                }

                int directorId = rs.getInt("director_id");
                if (!rs.wasNull()) {
                    Director director = new Director(
                            directorId,
                            rs.getString("director_name")
                    );
                    film.getDirectors().add(director);
                }
            }

            if (film == null) {
                log.warn("Фильм с id={} не найден", filmId);
                throw new ObjectNotFoundException("Фильм с id=" + filmId + " не найден");
            }
            return film;

        }, filmId);
    }

    @Override
    public Film addLikeFilm(int filmId, int userId) {
        Film film = findById(filmId);
        String checkSql = "SELECT COUNT(*) FROM film_like WHERE film_id = ? AND user_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, filmId, userId);
        if (count != null && count > 0) {
            log.debug("Лайк уже существует: filmId={}, userId={}", filmId, userId);
            return film;
        }
        String insertSql = "INSERT INTO film_like (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, userId, filmId);
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        return film;
    }

    @Override
    @Transactional
    public void checkFieldsFilm(Film film) {
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной");
            throw new IncorrectParameterException("Продолжительность фильма должна быть положительной");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не может быть пустым");
            throw new IncorrectParameterException("Название фильма не может быть пустым");
        }
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new IncorrectParameterException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getMpa() == null) {
            log.warn("Рейтинг обязателен");
            throw new IncorrectParameterException("Рейтинг обязателен");
        }
    }

    @Override
    @Transactional
    public void deleteLikeFilm(Integer id, Integer userId) {
        findById(id);
        String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        int rows = jdbcTemplate.update(sql, id, userId);
        if (rows == 0) {
            log.warn("Лайк не найден: filmId={}, userId={}", id, userId);
            throw new IncorrectParameterException("Лайк не найден");
        }
        log.info("Пользователь id={} удалил лайк у фильма id={}", userId, id);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        String sql = """
                SELECT f.film_id
                FROM films f
                LEFT JOIN film_like fl ON f.film_id = fl.film_id
                GROUP BY f.film_id
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT ?
                """;
        List<Integer> idPopularFilms = jdbcTemplate.queryForList(sql, Integer.class, count);
        return idPopularFilms.stream()
                .map(id -> findById(id))
                .toList();
    }

    @Override
    public List<Film> getCommonFilms(Integer userId, Integer friendId) {
        String sql = """
                SELECT f.film_id,
                       f.film_name,
                       f.description,
                       f.release_date,
                       f.duration,
                       r.rating_id,
                       r.rating_title,
                       g.genre_id,
                       g.genre_title,
                       d.director_id,
                       d.director_name,
                       COUNT(fl_all.user_id) AS popularity
                FROM films f
                JOIN film_like fl1 ON f.film_id = fl1.film_id
                JOIN film_like fl2 ON f.film_id = fl2.film_id
                LEFT JOIN film_like fl_all ON f.film_id = fl_all.film_id
                LEFT JOIN rating r ON f.rating_id = r.rating_id
                LEFT JOIN film_genre fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                LEFT JOIN film_director fd ON f.film_id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.director_id
                WHERE fl1.user_id = ?
                  AND fl2.user_id = ?
                GROUP BY f.film_id, g.genre_id
                ORDER BY popularity DESC, g.genre_id
                """;

        return jdbcTemplate.query(sql, rs -> {

            Map<Integer, Film> films = new LinkedHashMap<>();

            while (rs.next()) {

                int filmId = rs.getInt("film_id");

                Film film = films.get(filmId);

                if (film == null) {
                    film = new Film();
                    film.setId(filmId);
                    film.setName(rs.getString("film_name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));

                    int ratingId = rs.getInt("rating_id");
                    if (!rs.wasNull()) {
                        Rating rating = new Rating(
                                ratingId,
                                rs.getString("rating_title")
                        );
                        film.setMpa(rating);
                    }

                    film.setGenres(new LinkedHashSet<>());
                    film.setDirectors(new LinkedHashSet<>());
                    films.put(filmId, film);
                }
                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre(
                            genreId,
                            rs.getString("genre_title")
                    );
                    film.getGenres().add(genre);
                }

                int directorId = rs.getInt("director_id");
                if (!rs.wasNull()) {
                    Director director = new Director(
                            directorId,
                            rs.getString("director_name")
                    );
                    film.getDirectors().add(director);
                }
            }

            return new ArrayList<>(films.values());

        }, userId, friendId);
    }

    @Override
    public void delete(int filmId) {
        String sql = "DELETE FROM films WHERE film_id= ?";
        int rowsAffected = jdbcTemplate.update(sql, filmId);
        if (rowsAffected == 0) {
            log.warn("Фильм с id={} не найден для удаления.", filmId);
            throw new IncorrectParameterException("Фильм с указанным id не найден.");
        }
        log.info("Фильм с id={} успешно удалён.", filmId);
    }

    @Override
    public List<Film> findAllFilmsByDirectorIdSortByLikes(int directorId) {
        String sql = """
                SELECT f.film_id,
                       f.film_name,
                       f.description,
                       f.release_date,
                       f.duration,
                       r.rating_id,
                       r.rating_title,
                       g.genre_id,
                       g.genre_title,
                       d.director_id,
                       d.director_name,
                       COUNT(fl.user_id) AS popularity
                FROM films f
                JOIN film_director fd ON f.film_id = fd.film_id
                LEFT JOIN film_like fl ON f.film_id = fl.film_id
                LEFT JOIN rating r ON f.rating_id = r.rating_id
                LEFT JOIN film_genre fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                LEFT JOIN directors d ON fd.director_id = d.director_id
                WHERE fd.director_id = ?
                GROUP BY f.film_id,
                         r.rating_id,
                         g.genre_id,
                         d.director_id
                ORDER BY popularity DESC
                """;
        return jdbcTemplate.query(sql, rs -> {

            Map<Integer, Film> films = new LinkedHashMap<>();

            while (rs.next()) {

                int filmId = rs.getInt("film_id");

                Film film = films.get(filmId);

                if (film == null) {
                    film = new Film();
                    film.setId(filmId);
                    film.setName(rs.getString("film_name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));

                    int ratingId = rs.getInt("rating_id");
                    if (!rs.wasNull()) {
                        Rating rating = new Rating(
                                ratingId,
                                rs.getString("rating_title")
                        );
                        film.setMpa(rating);
                    }

                    film.setGenres(new LinkedHashSet<>());
                    film.setDirectors(new LinkedHashSet<>());
                    films.put(filmId, film);
                }
                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre(
                            genreId,
                            rs.getString("genre_title")
                    );
                    film.getGenres().add(genre);
                }

                int dirId = rs.getInt("director_id");
                if (!rs.wasNull()) {
                    Director director = new Director(
                            dirId,
                            rs.getString("director_name")
                    );
                    film.getDirectors().add(director);
                }
            }

            return new ArrayList<>(films.values());

        }, directorId);
    }

    @Override
    public List<Film> findAllFilmsByDirectorIdSortByYear(int directorId) {
        String sql = """
                SELECT f.film_id,
                       f.film_name,
                       f.description,
                       f.release_date,
                       f.duration,
                       r.rating_id,
                       r.rating_title,
                       g.genre_id,
                       g.genre_title,
                       d.director_id,
                       d.director_name
                FROM films f
                JOIN film_director fd ON f.film_id = fd.film_id
                LEFT JOIN rating r ON f.rating_id = r.rating_id
                LEFT JOIN film_genre fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                LEFT JOIN directors d ON fd.director_id = d.director_id
                WHERE fd.director_id = ?
                ORDER BY f.release_date DESC, f.film_id
                """;
        return jdbcTemplate.query(sql, rs -> {

            Map<Integer, Film> films = new LinkedHashMap<>();

            while (rs.next()) {

                int filmId = rs.getInt("film_id");

                Film film = films.get(filmId);

                if (film == null) {
                    film = new Film();
                    film.setId(filmId);
                    film.setName(rs.getString("film_name"));
                    film.setDescription(rs.getString("description"));
                    film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    film.setDuration(rs.getInt("duration"));

                    int ratingId = rs.getInt("rating_id");
                    if (!rs.wasNull()) {
                        Rating rating = new Rating(
                                ratingId,
                                rs.getString("rating_title")
                        );
                        film.setMpa(rating);
                    }

                    film.setGenres(new LinkedHashSet<>());
                    film.setDirectors(new LinkedHashSet<>());
                    films.put(filmId, film);
                }
                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    Genre genre = new Genre(
                            genreId,
                            rs.getString("genre_title")
                    );
                    film.getGenres().add(genre);
                }

                int dirId = rs.getInt("director_id");
                if (!rs.wasNull()) {
                    Director director = new Director(
                            dirId,
                            rs.getString("director_name")
                    );
                    film.getDirectors().add(director);
                }
            }

            return new ArrayList<>(films.values());

        }, directorId);
    }

    private void validateGenreExists(int genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);

        if (count == null || count == 0) {
            log.warn("Жанр с id={} не найден", genreId);
            throw new ObjectNotFoundException("Жанр с id=" + genreId + " не найден");
        }
    }

    private void validateRatingExists(int ratingId) {
        String sql = "SELECT COUNT(*) FROM rating WHERE rating_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ratingId);

        if (count == null || count == 0) {
            throw new ObjectNotFoundException("Рейтинг с id=" + ratingId + " не найден");
        }
    }

    private void saveFilmGenres(int filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            validateGenreExists(genre.getId());
            jdbcTemplate.update(sql, filmId, genre.getId());
        }
    }

    private void saveFilmDirector(int filmId, Set<Director> directors) {
        if (directors == null || directors.isEmpty()) {
            return;
        }
        String sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, directors, directors.size(),
                (ps, director) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, director.getId());
                });
    }


    private void updateFilmGenres(int filmId, Set<Genre> genres) {
        String deleteSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String insertSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(insertSql, genres, genres.size(),
                (ps, genre) -> {
                    validateGenreExists(genre.getId());
                    ps.setInt(1, filmId);
                    ps.setInt(2, genre.getId());
                });
    }

    private void updateFilmDirectors(int filmId, Set<Director> directors) {
        String deleteSql = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, filmId);
        if (directors == null || directors.isEmpty()) {
            return;
        }
        String insertSql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(insertSql, directors, directors.size(),
                (ps, director) -> {
                    validateGenreExists(director.getId());
                    ps.setInt(1, filmId);
                    ps.setInt(2, director.getId());
                });
    }
}
