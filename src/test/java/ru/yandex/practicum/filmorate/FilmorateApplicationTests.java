package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {
        User user = new User();
        user.setId(1);
        user.setEmail("friend@common.ru");
        user.setLogin(" ");
        user.setName("common");
        user.setBirthday(LocalDate.of(2000, 8, 20));
        UserController userController = new UserController();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
    }

    @Test
    void testName() {
        User user = new User();
        user.setId(1);
        user.setEmail("friend@common.ru");
        user.setLogin("common");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 8, 20));
        UserController userController = new UserController();
        userController.createUser(user);
        String actualUserName = user.getName();
        assertEquals("common", actualUserName);
    }

    @Test
    void testEmail() {
        User user = new User();
        user.setId(1);
        user.setLogin("common");
        user.setName("common");
        user.setBirthday(LocalDate.of(2000, 8, 20));
        user.setEmail("friendcommon.ru");
        UserController userController = new UserController();
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );
        assertEquals("Адрес электронной почты должен содержать символ @",
                exception.getMessage());
    }

    @Test
    void testFilmNameNotNull() {
        Film film = new Film();
        film.setId(1);
        film.setName(" ");
        film.setDuration(100);
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2000, 8, 20));
        FilmController filmController = new FilmController();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

    @Test
    void testFilmDuration() {
        Film film = new Film();
        film.setId(1);
        film.setName("Film name");
        film.setDuration(-50);
        film.setDescription("Film description");
        film.setReleaseDate(LocalDate.of(2000, 8, 20));
        FilmController filmController = new FilmController();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
    }

}
