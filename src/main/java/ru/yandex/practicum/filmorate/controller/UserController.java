package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Сформирован запрос на получение всех пользователей");
        return userService.getAll();
    }

    @GetMapping("{id}")
    public User findById(@PathVariable("id") int id) {
        log.info("Сформирован запрос на получение пользователя с id={}", id);
        return userService.findById(id);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя user={}", user);
        return userService.update(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("Получен запрос на добавление пользователю id={} друга с id={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable("id") int id) {
        log.info("Получен запрос на удаление пользователя с id={}", id);
        userService.delete(id);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        log.info("Получен запрос на удаление у пользователя с id={} друга с id={}", id, friendId);
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriendsThisUser(@PathVariable("id") int id) {
        log.info("Получен запрос на выборку друзей пользователя с id={}", id);
        return userService.getFriendsThisUser(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        log.info("Получен запрос на выборку общих друзей пользователей с id={} и id={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
