package ru.yandex.practicum.filmorate.controller;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private int nextIdUser = 1;

    @GetMapping
    public Collection<User> findAll() {
        log.debug("Текущее количество пользователей: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if(users.containsKey(user.getEmail())) {
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        validateUser(user);
        user.setId(nextIdUser++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {

        if (user.getId() <= 0) {
            throw new ValidationException("Id пользователя должен быть указан");
        }

        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден");
        }

        validateUser(user);

        existingUser.setName(user.getName());
        existingUser.setLogin(user.getLogin());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthday(user.getBirthday());
        //users.put(existingUser.getEmail(), existingUser);
        return existingUser;
    }

    private void validateUser(User user) {
        if(user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if(!user.getEmail().contains("@")) {
            throw new ValidationException("Адрес электронной почты должен содержать символ @");
        }
        if(user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if(user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if(user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }
}
