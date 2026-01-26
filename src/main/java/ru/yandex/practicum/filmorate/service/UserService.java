package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
public class UserService {
    UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(int userId) {
        userStorage.deleteUser(userId);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriends(userId, friendId);
    }

    public List<User> getFriendsThisUser(Integer userId) {
        return userStorage.getFriendsThisUser(userId);
    }

    public void deleteFriends(int userId, int friendId) {
        userStorage.deleteFriends(userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}
