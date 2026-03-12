package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.repository.FeedStorage;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.List;

@Service
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    @Autowired
    public FeedService(FeedStorage feedStorage, UserStorage userStorage) {
        this.feedStorage = feedStorage;
        this.userStorage = userStorage;
    }

    public List<Feed> getFeed(int id) {
        userStorage.findById(id);
        List<Feed> feeds = feedStorage.getFeed(id);
        feeds.forEach(f -> f.setTimestamp(f.getTimestamp())); // можно оставить миллисекунды
        return feeds;
    }

    public void addEvent(int userId, String eventType, String operation, int entityId) {
        feedStorage.addEvent(userId, eventType, operation, entityId);
    }
}
