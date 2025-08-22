package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }


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

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public void putNewFriend(@PathVariable("id") int userId,
                             @PathVariable("friendId") int friendId) {

        userService.addFriend(userStorage, userId, friendId);
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable("id") int userId,
                                   @PathVariable("friendId") int friendId) {

        return userService.deleteFriend(userStorage, userId, friendId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") int userId) {

        return userService.getFriends(userStorage, userId);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int userId,
                                       @PathVariable("otherId") int otherId) {

        return userService.getCommonFriends(userStorage, userId, otherId);
    }

}