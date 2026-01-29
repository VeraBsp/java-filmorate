package ru.yandex.practicum.filmorate.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private static final LocalDate RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> filmsStorage = new HashMap<>();
    private int nextId = 1;

    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Film createFilm(Film film) {
        checkFieldsFilm(film);
        film.setId(nextId++);
        filmsStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAllFilm() {
        log.info("Текущее количество фильмов: {}", filmsStorage.size());
        return List.copyOf(filmsStorage.values());
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() <= 0) {
            log.warn("Id фильма должен быть указан");
            throw new IncorrectParameterException("Id фильма должен быть указан");
        }

        Film existingFilm = filmsStorage.get(film.getId());
        if (existingFilm == null) {
            log.warn("Фильм с id=" + film.getId() + " не найден");
            throw new ObjectNotFoundException("Фильм с id=" + film.getId() + " не найден");
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
            log.warn("Фильм с введенным id не найден");
            throw new ObjectNotFoundException("Фильм с id=" + filmId + " не найден");
        }
        return film;
    }

    @Override
    public Film addLikeFilm(int filmId, int userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(filmId);
        film.getLikes().add(user.getId());
        return film;
    }

    @Override
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
    }

    @Override
    public void deleteLikeFilm(Integer id, Integer userId) {
        User user = userStorage.findUserById(userId);
        Film film = findFilmById(id);
        film.getLikes().remove(user.getId());
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        return filmsStorage.values()
                .stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .toList();
    }
}
