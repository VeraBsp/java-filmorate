package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final Map<Integer, User> userStorage = new HashMap<>();
    private int nextIdUser = 1;

    private void validateEmailUniqueness(User user) {
        boolean emailExists = userStorage.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail())
                        && u.getId() != user.getId());

        if (emailExists) {
            log.info("Пользователь с электронной почтой уже зарегистрирован.");
            throw new IncorrectParameterException(
                    String.format("Пользователь с электронной почтой %s уже зарегистрирован.", user.getEmail())
            );
        }
    }

    @Override
    public User createUser(@Valid User user) {
        validateEmailFormat(user);
        validateEmailUniqueness(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(nextIdUser++);
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        User removed = userStorage.remove(userId);
        if (removed == null) {
            log.warn("Id пользователя указан некорректно");
            throw new IncorrectParameterException("Id пользователя указан некорректно");
        }
    }

    @Override
    public void validateEmailFormat(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Некорректный адрес электронной почты.");
            throw new IncorrectParameterException("Некорректный адрес электронной почты.");
        }
    }

    @Override
    public User updateUser(@Valid User user) {
        validateEmailFormat(user);
        if (user.getId() <= 0) {
            log.warn("Id пользователя должен быть указан");
            throw new IncorrectParameterException("Id пользователя указан некорректно");
        }
        User existingUser = userStorage.get(user.getId());
        if (existingUser == null) {
            log.warn("Пользователь с id=" + user.getId() + " не найден");
            throw new ObjectNotFoundException("Пользователь с id=" + user.getId() + " не найден");
        }
        validateEmailUniqueness(user);
        if (user.getName() == null || user.getName().isBlank()) {
            existingUser.setName(user.getLogin());
        } else {
            existingUser.setName(user.getName());
        }
        existingUser.setLogin(user.getLogin());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthday(user.getBirthday());
        return existingUser;
    }

    @Override
    public User findUserById(int userId) {
        User user = userStorage.get(userId);
        if (user == null) {
            log.warn("Пользователь с id=" + userId + " не найден");
            throw new ObjectNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        return user;
    }

    @Override
    public void addFriends(Integer userId, Integer friendId) {
//        if (userId.equals(friendId)) {
//            log.warn("Указаны одинаковые Id пользователей. Попытка добавления самого себя в друзья");
//            throw new IncorrectParameterException("Указаны одинаковые Id пользователей. Попытка добавления самого себя в друзья");
//        }
//        User user = findUserById(userId);
//        User friend = findUserById(friendId);
//        if (user.getFriends().contains(friendId)) {
//            log.info("Пользователи уже друзья");
//            throw new IncorrectParameterException("Пользователи уже друзья");
//        }
//        user.getFriends().add(friendId);
//        friend.getFriends().add(userId);
    }

    @Override
    public void deleteFriends(Integer userId, Integer friendId) {
//        User user = findUserById(userId);
//        User friend = findUserById(friendId);
//        user.getFriends().remove(friendId);
//        friend.getFriends().remove(userId);
    }

    @Override
    public List<User> getFriendsThisUser(Integer userId) {
//        User user = findUserById(userId);
//        return user.getFriends().stream()
//                .map(userStorage::get)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
        return List.of();
    }

    @Override
    public List<User> getCommonFriends(Integer userId, Integer otherId) {
//        User user = findUserById(userId);
//        User otherUser = findUserById(otherId);
//        return user.getFriends().stream()
//                .filter(e -> otherUser.getFriends().contains(e))
//                .map(id -> findUserById(id))
//                .collect(Collectors.toList());
        return List.of();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }
}
