package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users;
    private final Logger usersLog;

    public InMemoryUserStorage() {
        this.usersLog = LoggerFactory.getLogger(this.getClass());
        this.users = new HashMap<Integer, User>();
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {

        int newId = getNextId();
        user.setId(newId);
        users.put(newId, user);

        return user;
    }

    @Override
    public User update(User newUser) {

        int userId = newUser.getId();
        if (!users.containsKey(userId)) {
            String errorMessage = "Пользователь с id " + userId + " отсутствует";

            usersLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        users.put(userId, newUser);

        return newUser;
    }

    @Override
    public void remove(User user) {

        Integer userId = user.getId();
        if (!users.containsKey(userId)) {
            String errorMessage = "Пользователь с id " + userId + " отсутствует";

            usersLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            users.remove(userId);
        }
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @Override
    public User getUserById(Integer id) {
        User user = users.get(id);
        if (user == null) {
            String errorMessage = "Пользователь с id " + id + " не найден";

            usersLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        return user;
    }

    @Override
    public User updateFriends(User user) {
        return null;
    }

}
