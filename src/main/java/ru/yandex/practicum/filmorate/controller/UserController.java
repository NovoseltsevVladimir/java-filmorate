package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.checkers.UserChecker;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Integer, User> users = new HashMap<>();
    private final Logger usersLog = LoggerFactory.getLogger(User.class);

    //получение всех фильмов.
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    //добавление фильма;
    @PostMapping
    public User create(@RequestBody User user) {

        if (!UserChecker.checkAndLogUser(user, usersLog)) {
            throw new ValidationException("Валидация не пройдена");
        }

        int newId = getNextId();
        user.setId(newId);
        user.replaceBlankNameToLogin();
        users.put(newId, user);

        return user;
    }

    //обновление фильма;
    @PutMapping
    public User update(@RequestBody User newUser) {
        //Если проверка не пройдена - не фильм не обновляем
        if (!UserChecker.checkAndLogUser(newUser, usersLog)) {
            throw new ValidationException("Валидация не пройдена");
        }

        int filmId = newUser.getId();
        if (!users.containsKey(filmId)) {
            throw new ValidationException("Пользователь с id " + filmId + " отсутствует");
        }

        newUser.replaceBlankNameToLogin();
        users.put(filmId, newUser);

        return newUser;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}