package ru.yandex.practicum.filmorate.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> filmsStorage = new HashMap<>();
    private int nextId = 1;

    private final InMemoryUserStorage inMemoryUserStorage;

    public InMemoryFilmStorage(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }


    @Override
    public Film createFilm(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            log.info("Дата релиза не может быть раньше " + RELEASE_DATE);
            throw new FilmReleaseDateIvalid("Дата релиза не может быть раньше " + RELEASE_DATE);
        }
        checkFieldsFilm(film);
        film.setId(nextId++);
        filmsStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", filmsStorage.size());
        return filmsStorage.values();
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            log.info("Id фильма должен быть указан");
            throw new ValidationException("Id фильма должен быть указан");
        }

        Film existingFilm = filmsStorage.get(film.getId());
        if (existingFilm == null) {
            log.info("Фильм с id=" + film.getId() + " не найден");
            throw new FilmNotFoundException("Фильм с id=" + film.getId() + " не найден");
        }
        if (film.getReleaseDate().isBefore(RELEASE_DATE)) {
            log.info("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        checkFieldsFilm(film);
        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        return existingFilm;
    }

    @Override
    public Film findFilmById(int filmId) {
        Film film = filmsStorage.get(filmId);
        if (film == null) {
            log.info("Фильм с введенным id не найден");
            throw new FilmNotFoundException("Фильм с id=" + filmId + " не найден");
        }
        return film;
    }

    @Override
    public Film addLikeFilm(int filmId, int userId) {
        inMemoryUserStorage.findUserById(userId);
        Film film = findFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public void checkFieldsFilm(Film film) {
        if (film.getDuration() <= 0) {
            log.info("Продолжительность фильма должна быть положительной");
            throw new InvalidFilmDurationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getName().isBlank() || film.getName().isEmpty()) {
            log.info("Название фильма не может быть пустым");
            throw new InvalidFilmNameException("Название фильма не может быть пустым");
        }
    }

    @Override
    public void deleteLikeFilm(Integer id, Integer userId) {
        inMemoryUserStorage.findUserById(userId);
        Film film = findFilmById(id);
        film.getLikes().remove(id);
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        return filmsStorage.values()
                .stream()
                .sorted(Comparator.<Film>comparingInt(film -> film.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
