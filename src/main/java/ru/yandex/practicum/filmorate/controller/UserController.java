package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserStorage userStorage = new InMemoryUserStorage();

    //получение всех пользователей.
    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    //добавление пользователя;
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userStorage.create(user);
    }

    //обновление пользователя;
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userStorage.update(newUser);
    }

    public void validateLogin(User user) {
       userStorage.validateLogin(user);
    }
}