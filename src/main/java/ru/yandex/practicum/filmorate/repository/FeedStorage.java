package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    List<Feed> getFeed(int id);

    void addEvent(int userId, String eventType, String operation, int entityId);
}
