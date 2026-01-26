package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.InvalidEmailException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> userStorage = new HashMap<>();
    private int nextIdUser = 1;


    @Override
    public User createUser(@Valid User user) {
        checkEmail(user);
        boolean emailExists = userStorage.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));

        if (emailExists) {
            log.info("Пользователь с электронной почтой уже зарегистрирован.");
            throw new UserAlreadyExistException(String.format(
                    "Пользователь с электронной почтой %s уже зарегистрирован.",
                    user.getEmail()
            ));
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(nextIdUser++);
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        if (userStorage.containsKey(userId)) {
            userStorage.remove(userId);
        } else {
            log.info("Id пользователя указан некорректно");
            throw new IncorrectParameterException("Id пользователя указан некорректно");
        }
    }

    @Override
    public void checkEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.info("Адрес электронной почты не может быть пустым.");
            throw new InvalidEmailException("Адрес электронной почты не может быть пустым.");
        }
    }

    @Override
    public User updateUser(@Valid User user) {
        checkEmail(user);
        if (user.getId() <= 0) {
            log.info("Id пользователя должен быть указан");
            throw new IncorrectParameterException("Id пользователя указан некорректно");
        }

        User existingUser = userStorage.get(user.getId());
        if (existingUser == null) {
            log.info("Пользователь с id=" + user.getId() + " не найден");
            throw new UserNotFoundException("Пользователь с id=" + user.getId() + " не найден");
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

    @Override
    public User findUserById(int userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            log.info("Пользователь с id=" + userId + " не найден");
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return user;
    }

    @Override
    public void addFriends(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriends(Integer userId, Integer friendId) {
        User user = findUserById(userId);
        User friend = findUserById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriendsThisUser(Integer userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            log.info("Пользователь с id=" + userId + " не найден");
            throw new UserNotFoundException("Пользователь с id=" + userId + " не найден");
        }

        return user.getFriends().stream()
                .map(userStorage::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = findUserById(userId);
        User otherUser = findUserById(otherId);
        List<User> commonFriend = new ArrayList<>();
        for (Integer friendUserId : user.getFriends()) {
            if (otherUser.getFriends().contains(friendUserId)) {
                commonFriend.add(userStorage.get(friendUserId));
            }
        }
        return commonFriend;
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.values();
    }
}
