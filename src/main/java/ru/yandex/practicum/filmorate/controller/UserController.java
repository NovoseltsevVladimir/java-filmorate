package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //получение всех пользователей.
    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    //добавление пользователя;
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    //обновление пользователя;
    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return userService.update(newUser);
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public void putNewFriend(@PathVariable("id") int userId,
                             @PathVariable("friendId") int friendId) {

        userService.addFriend(userId, friendId);
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriend(@PathVariable("id") int userId,
                                   @PathVariable("friendId") int friendId) {

        return userService.deleteFriend(userId, friendId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable("id") int userId) {

        return userService.getFriends(userId);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int userId,
                                       @PathVariable("otherId") int otherId) {

        return userService.getCommonFriends(userId, otherId);
    }

}