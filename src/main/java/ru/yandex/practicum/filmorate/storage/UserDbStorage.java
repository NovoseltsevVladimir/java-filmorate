package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final UserRepository repository;
    private final Logger log;

    @Autowired
    public UserDbStorage(UserRepository repository) {
        this.repository = repository;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public Collection<User> findAll() {
        return repository.findAll()
                .stream()
                .map(user -> fillUserFriends(user))
                .collect(Collectors.toList());
    }

    @Override
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        int userId = user.getId();
        Optional<User> oldUser = repository.findById(userId);

        if (oldUser.isEmpty()) {
            String errorMessage = "Пользователь с id " + userId + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        repository.update(user);

        return user;
    }

    @Override
    public void remove(User user) {
        int userId = user.getId();
        Optional<User> optionalUser = repository.findById(userId);

        if (optionalUser.isEmpty()) {
            String errorMessage = "Пользователь с id " + userId + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        repository.delete(userId);
    }

    @Override
    public void removeFriend(User user, Integer friendId) {

        if (user.getFriends().contains(friendId)) {
            repository.deleteFriend(user.getId(), friendId);
        } else {
            String errorMessage = "Пользователь с id " + friendId + " не добавлен в друзья";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    @Override
    public User getUserById(Integer id) {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            String errorMessage = "Пользователь с id " + id + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            User user = fillUserFriends(optionalUser.get());
            return user;
        }
    }

    @Override
    public User updateFriends(User user) {
        return repository.updateFriends(user);
    }

    @Override
    public List<User> getFriends(User user) {
        return repository.getFriends(user)
                .stream()
                .map(id -> getUserById(id))
                .collect(Collectors.toList());
    }

    private User fillUserFriends(User user) {

        user.setFriends(new HashSet<>(repository.getFriends(user)));
        return user;
    }
}
