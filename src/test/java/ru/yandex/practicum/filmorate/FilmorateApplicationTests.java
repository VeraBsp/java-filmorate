package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.repository.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.repository.impl.RatingDbStorage;
import ru.yandex.practicum.filmorate.repository.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDbStorage.class, RatingDbStorage.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private User user1;
    private User user2;
    private User friend1;
    private User friend2;
    private Film film1;
    private Film film2;
    private GenreDbStorage genreDbStorage;
    Genre genre1;
    Genre genre2;
    Rating rating;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setLogin("user1Login");
        user1.setEmail("user1@mail.ru");
        user1.setName("user1 Name");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        user2 = new User();
        user2.setLogin("user2Login");
        user2.setEmail("user2@mail.ru");
        user2.setName("user2 Name");
        user2.setBirthday(LocalDate.of(2000, 2, 2));
        friend1 = new User();
        friend1.setLogin("friend1Login");
        friend1.setEmail("friend1@mail.ru");
        friend1.setName("friend1 Name");
        friend1.setBirthday(LocalDate.of(2012, 1, 11));
        friend2 = new User();
        friend2.setLogin("friend2Login");
        friend2.setEmail("friend2@mail.ru");
        friend2.setName("friend2 Name");
        friend2.setBirthday(LocalDate.of(2012, 10, 1));
        film1 = new Film();
        film1.setName("Matrix");
        film1.setDescription("Sci-fi");
        film1.setDuration(136);
        film1.setReleaseDate(LocalDate.of(1999, 3, 31));

        rating = new Rating();
        rating.setId(1); // должен существовать в data.sql
        film1.setMpa(rating);

        genre1 = new Genre();
        genre1.setId(1);

        genre2 = new Genre();
        genre2.setId(2);

        film1.setGenres(Set.of(genre1, genre2));
        film2 = new Film();
        film2.setName("Matrix1");
        film2.setDescription("Sci-fi1");
        film2.setDuration(136);
        film2.setReleaseDate(LocalDate.of(2000, 3, 31));
        film2.setMpa(rating);
        film2.setGenres(Set.of(genre1, genre2));
    }

    @Test
    public void testFindUserById() {
        userStorage.create(user1);
        User foundUser = userStorage.findById(user1.getId());

        assertThat(foundUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", user1.getId());
    }

    @Test
    public void testCreateUser() {
        User createdUser = userStorage.create(user1);
        assertThat(createdUser)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("login", "user1Login")
                .hasFieldOrPropertyWithValue("email", "user1@mail.ru")
                .hasFieldOrPropertyWithValue("name", "user1 Name");

        User userFromDb = userStorage.findById(createdUser.getId());
        assertThat(userFromDb)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", createdUser.getId())
                .hasFieldOrPropertyWithValue("email", "user1@mail.ru");
    }

    @Test
    public void testCreateUserWithoutName() {
        user1.setName("");
        User createdUser = userStorage.create(user1);
        assertThat(createdUser.getName()).isEqualTo("user1Login");
    }

    @Test
    public void testCreateUserWithDuplicateEmail() {
        userStorage.create(user1);
        user2.setEmail("user1@mail.ru");
        assertThatThrownBy(() -> userStorage.create(user2))
                .isInstanceOf(IncorrectParameterException.class);
    }

    @Test
    public void testDeleteUser() {
        User createdUser = userStorage.create(user1);
        userStorage.delete(createdUser.getId());
        assertThatThrownBy(() -> userStorage.findById(createdUser.getId()))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    public void testUpdateUser() {
        User createdUser = userStorage.create(user1);

        int originalId = createdUser.getId();
        String originalLogin = createdUser.getLogin();
        LocalDate originalBirthday = createdUser.getBirthday();
        createdUser.setName("Updated Name");

        User updatedUser = userStorage.update(createdUser);
        assertThat(updatedUser.getId()).isEqualTo(originalId);
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getLogin()).isEqualTo(originalLogin);
        assertThat(updatedUser.getEmail()).isEqualTo("user1@mail.ru");
        assertThat(updatedUser.getBirthday()).isEqualTo(originalBirthday);

        User userFromDb = userStorage.findById(originalId);

        assertThat(userFromDb.getId()).isEqualTo(originalId);
        assertThat(userFromDb.getName()).isEqualTo("Updated Name");
        assertThat(userFromDb.getLogin()).isEqualTo(originalLogin);
        assertThat(userFromDb.getEmail()).isEqualTo("user1@mail.ru");
        assertThat(userFromDb.getBirthday()).isEqualTo(originalBirthday);
    }

    @Test
    public void testAddFriend_success() {
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);
        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());
        List<User> friends = userStorage.getFriendsThisUser(createdUser1.getId());
        assertThat(friends)
                .isNotEmpty()
                .extracting(User::getId)
                .contains(createdUser2.getId());
    }

    @Test
    public void testAddFriend_selfFriend() {
        User createdUser = userStorage.create(user1);

        assertThatThrownBy(() ->
                userStorage.addFriends(createdUser.getId(), createdUser.getId())
        ).isInstanceOf(IncorrectParameterException.class);
    }

    @Test
    public void testAddFriend_duplicate() {
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);

        userStorage.addFriends(createdUser1.getId(), createdUser2.getId());

        assertThatThrownBy(() ->
                userStorage.addFriends(createdUser1.getId(), createdUser2.getId())
        ).isInstanceOf(IncorrectParameterException.class);
    }

    @Test
    public void testAddFriend_userNotFound() {
        User createdUser = userStorage.create(user1);

        assertThatThrownBy(() ->
                userStorage.addFriends(createdUser.getId(), 9999)
        ).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void testGetFriends_success() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.addFriends(user1.getId(), user2.getId());

        List<User> friends = userStorage.getFriendsThisUser(user1.getId());

        assertThat(friends)
                .isNotEmpty()
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(user2.getId());
    }

    @Test
    void testGetFriends_emptyList() {
        userStorage.create(user1);
        List<User> friends = userStorage.getFriendsThisUser(user1.getId());
        assertThat(friends).isEmpty();
    }

    @Test
    void testGetCommonFriends_empty() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(friend1);
        // только user1 добавляет друга
        userStorage.addFriends(user1.getId(), friend1.getId());
        List<User> commonFriends = userStorage.getCommonFriends(user1.getId(), user2.getId());
        assertThat(commonFriends).isEmpty();
    }

    @Test
    void testGetCommonFriendsOne_success() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(friend1);

        userStorage.addFriends(user1.getId(), friend1.getId());
        userStorage.addFriends(user2.getId(), friend1.getId());
        List<User> commonFriends =
                userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends)
                .isNotEmpty()
                .hasSize(1)
                .extracting(User::getId)
                .containsExactly(friend1.getId());
    }

    @Test
    void testGetCommonFriends_multiple() {
        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(friend1);
        userStorage.create(friend2);
        userStorage.addFriends(user1.getId(), friend1.getId());
        userStorage.addFriends(user1.getId(), friend2.getId());

        userStorage.addFriends(user2.getId(), friend1.getId());
        userStorage.addFriends(user2.getId(), friend2.getId());

        List<User> commonFriends =
                userStorage.getCommonFriends(user1.getId(), user2.getId());

        assertThat(commonFriends)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(friend1.getId(), friend2.getId());
    }

    @Test
    void testGetAllUsers_success() {
        User createdUser1 = userStorage.create(user1);
        User createdUser2 = userStorage.create(user2);
        List<User> users = userStorage.getAll();
        assertThat(users)
                .isNotEmpty()
                .hasSizeGreaterThanOrEqualTo(2)
                .extracting(User::getId)
                .contains(createdUser1.getId(), createdUser2.getId());
    }

    @Test
    void testCreateFilm_success() {
        film1 = new Film();
        film1.setName("Test Film");
        film1.setDescription("Test description");
        film1.setDuration(120);
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));

        Rating mpa = new Rating();
        mpa.setId(1);
        mpa.setName("G");
        film1.setMpa(mpa);
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");

        film1.setGenres(Set.of(genre));

        Film createdFilm = filmStorage.create(film1);

        assertThat(createdFilm)
                .isNotNull()
                .hasFieldOrProperty("id")
                .hasFieldOrPropertyWithValue("name", "Test Film")
                .hasFieldOrPropertyWithValue("description", "Test description")
                .hasFieldOrPropertyWithValue("duration", 120)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2020, 1, 1));

        Film filmFromDb = filmStorage.findById(createdFilm.getId());

        assertThat(filmFromDb)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", createdFilm.getId())
                .hasFieldOrPropertyWithValue("name", "Test Film");
    }

    @Test
    void testFindFilmById_success() {
        Film created = filmStorage.create(film1);
        Film found = filmStorage.findById(created.getId());
        assertThat(found)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", created.getId())
                .hasFieldOrPropertyWithValue("name", "Matrix")
                .hasFieldOrPropertyWithValue("duration", 136);

        assertThat(found.getMpa())
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1);

        assertThat(found.getGenres())
                .isNotNull()
                .hasSize(2)
                .extracting(Genre::getId)
                .containsExactly(1, 2); // порядок важен (ORDER BY genre_id)
    }

    @Test
    void testFindFilmById_notFound() {
        assertThatThrownBy(() -> filmStorage.findById(9999))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("не найден");
    }

    @Test
    void testUpdateFilm_success() {
        Film film = new Film();
        film.setName("Old Name");
        film.setDescription("Old description");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        Rating rating = new Rating();
        rating.setId(1);
        film.setMpa(rating);

        Genre genre1 = new Genre();
        genre1.setId(1);
        film.setGenres(Set.of(genre1));

        Film created = filmStorage.create(film);

        created.setName("New Name");
        created.setDescription("New description");
        created.setDuration(150);
        created.setReleaseDate(LocalDate.of(2010, 5, 5));

        Rating newRating = new Rating();
        newRating.setId(2);
        created.setMpa(newRating);

        Genre genre2 = new Genre();
        genre2.setId(2);
        created.setGenres(Set.of(genre2));

        Film updated = filmStorage.update(created);

        assertThat(updated.getId()).isEqualTo(created.getId());

        assertThat(updated)
                .hasFieldOrPropertyWithValue("name", "New Name")
                .hasFieldOrPropertyWithValue("description", "New description")
                .hasFieldOrPropertyWithValue("duration", 150)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2010, 5, 5));

        assertThat(updated.getMpa().getId()).isEqualTo(2);

        assertThat(updated.getGenres())
                .hasSize(1)
                .extracting(Genre::getId)
                .containsExactly(2);
    }

    @Test
    void testUpdateFilm_invalidId() {
        Film film = new Film();
        film.setId(77); // некорректный id

        assertThatThrownBy(() -> filmStorage.update(film))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Фильм с id=77 не найден");
    }

    @Test
    void testAddLikeFilm_filmNotFound() {
        User user = userStorage.create(user1);

        assertThatThrownBy(() ->
                filmStorage.addLikeFilm(9999, user.getId())
        ).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void shouldReturnCommonFilmsSortedByPopularity() {
        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
        filmStorage.create(film1);
        filmStorage.create(film2);

        filmStorage.addLikeFilm(film1.getId(), user1.getId());
        filmStorage.addLikeFilm(film2.getId(), user1.getId());

        filmStorage.addLikeFilm(film1.getId(), user2.getId());
        filmStorage.addLikeFilm(film2.getId(), user2.getId());

        // добавим дополнительный лайк filmB (чтобы он стал популярнее)
        User extraUser = new User();
        extraUser.setLogin("extra");
        extraUser.setEmail("extra@mail.ru");
        extraUser.setName("extra");
        extraUser.setBirthday(LocalDate.of(1995, 5, 5));
        extraUser = userStorage.create(extraUser);

        filmStorage.addLikeFilm(film2.getId(), extraUser.getId());

        List<Film> result = filmStorage.getCommonFilms(user1.getId(), user2.getId());

        assertEquals(2, result.size());
        assertEquals("Matrix1", result.get(0).getName()); // более популярный
        assertEquals("Matrix", result.get(1).getName());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonFilms() {
        user1 = userStorage.create(user1);
        user2 = userStorage.create(user2);
        filmStorage.create(film1);
        filmStorage.create(film2);
        filmStorage.addLikeFilm(film1.getId(), user1.getId());
        filmStorage.addLikeFilm(film2.getId(), user2.getId());

        List<Film> result = filmStorage.getCommonFilms(user1.getId(), user2.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteFilm() {
        Film createdFilm = filmStorage.create(film1);
        filmStorage.delete(createdFilm.getId());
        assertThatThrownBy(() -> filmStorage.findById(createdFilm.getId()))
                .isInstanceOf(ObjectNotFoundException.class);
    }
}
