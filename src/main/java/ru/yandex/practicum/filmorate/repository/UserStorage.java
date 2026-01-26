package ru.yandex.practicum.filmorate.repository;

//Создайте интерфейсы FilmStorage и UserStorage, в которых будут определены методы добавления, удаления и
// модификации объектов.

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User createUser(User user);

    void deleteUser(int userID);

    void checkEmail(User user);

    User updateUser(User user);

    User findUserById(int userId);

    void addFriends(Integer userId, Integer friendId);

    void deleteFriends(Integer userId, Integer friendId);

    List<User> getFriendsThisUser(Integer userId);

    List<User> getCommonFriends(Integer userId, Integer otherId);

    Collection<User> findAll();
}
