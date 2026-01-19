package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

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
        if (users.containsKey(user.getEmail())) {
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(nextIdUser++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() <= 0) {
            log.info("Id пользователя должен быть указан");
            throw new ValidationException("Id пользователя должен быть указан");
        }

        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            log.info("Пользователь с id=" + user.getId() + " не найден");
            throw new ValidationException("Пользователь с id=" + user.getId() + " не найден");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            existingUser.setName(user.getLogin());
        }
        existingUser.setName(user.getName());
        existingUser.setLogin(user.getLogin());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthday(user.getBirthday());
        return existingUser;
    }
}
