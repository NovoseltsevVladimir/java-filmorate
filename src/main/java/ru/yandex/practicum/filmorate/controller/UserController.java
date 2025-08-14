package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
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

    //получение всех пользователей.
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    //добавление пользователя;
    @PostMapping
    public User create(@Valid @RequestBody User user) {

        validateLogin(user);

        int newId = getNextId();
        user.setId(newId);
        replaceBlankNameToLogin(user);
        users.put(newId, user);

        return user;
    }

    //обновление пользователя;
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        //Если проверка не пройдена - пользователя не обновляем
        validateLogin(newUser);

        int userId = newUser.getId();
        if (!users.containsKey(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " отсутствует");
        }

        replaceBlankNameToLogin(newUser);
        users.put(userId, newUser);

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

    private void replaceBlankNameToLogin(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin().contains(" ")) {
            usersLog.warn("Логин не может содержать пробелы");
            throw new ValidationException("Валидация не пройдена");
        }
    }

    public void clearUsers() {
        users.clear();
    }
}