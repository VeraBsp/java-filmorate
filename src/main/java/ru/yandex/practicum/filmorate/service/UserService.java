package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FeedService feedService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FeedService feedService) {
        this.userStorage = userStorage;
        this.feedService = feedService;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User findById(int id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(int userId) {
        userStorage.delete(userId);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriends(userId, friendId);
        feedService.addEvent(userId, "FRIEND", "ADD", friendId);
    }

    public List<User> getFriendsThisUser(Integer userId) {
        return userStorage.getFriendsThisUser(userId);
    }

    public void deleteFriends(int userId, int friendId) {
        userStorage.deleteFriends(userId, friendId);
        feedService.addEvent(userId, "FRIEND", "REMOVE", friendId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}
