package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component()
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage{

    private final UserRepository repository;
    private final Logger log;

    public UserDbStorage(UserRepository repository) {
        this.repository = repository;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public Collection<User> findAll() {
        return repository.findAll();
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
        Optional<User> optionalUser= repository.findById(userId);

        if (optionalUser.isEmpty()) {
            String errorMessage = "Пользователь с id " + userId + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        repository.delete(userId);
    }

    @Override
    public User getUserById(Integer id) {
        Optional<User> optionalUser = repository.findById(id);
        if (optionalUser.isEmpty()) {
            String errorMessage = "Пользователь с id " + id + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            return optionalUser.get();
        }
    }





}
