package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    void deleteUser(int userID);

    void validateEmailFormat(User user);

    User updateUser(User user);

    User findUserById(int userId);

    void addFriends(Integer userId, Integer friendId);

    void deleteFriends(Integer userId, Integer friendId);

    List<User> getFriendsThisUser(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherId);

    List<User> getAllUsers();
}
