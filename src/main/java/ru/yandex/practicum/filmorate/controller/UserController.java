package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
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
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    //добавление пользователя;
    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserRequest request) {
        return userService.create(request);
    }

    //обновление пользователя;
    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest request) {
        return userService.update(request);
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    @PutMapping("/{id}/friends/{friendId}")
    public List<UserDto> putNewFriend(@PathVariable("id") int userId,
                                      @PathVariable("friendId") int friendId) {

        return userService.addFriend(userId, friendId);
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    @DeleteMapping("/{id}/friends/{friendId}")
    public List<UserDto> deleteFriend(@PathVariable("id") int userId,
                                      @PathVariable("friendId") int friendId) {

        return userService.deleteFriend(userId, friendId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable("id") int userId) {

        return userService.getFriends(userId);
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable("id") int userId,
                                          @PathVariable("otherId") int otherId) {

        return userService.getCommonFriends(userId, otherId);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") int userId) {

        return userService.getById(userId);
    }

}