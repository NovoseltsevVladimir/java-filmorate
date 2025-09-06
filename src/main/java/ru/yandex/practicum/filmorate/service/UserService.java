package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage = null;
    private final Logger usersLog = LoggerFactory.getLogger(this.getClass());

    public Collection<UserDto> findAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    //добавление пользователя;
    public UserDto create(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);

        validateLogin(user);
        replaceBlankNameToLogin(user);
        checkAndInitializeLists(user); //инициализация списков, если не установлены
        user = userStorage.create(user);

        return UserMapper.mapToUserDto(user);
    }

    //обновление пользователя;
    public UserDto update(UpdateUserRequest request) {
        User user = userStorage.getUserById(request.getId());
        User newUser = UserMapper.updateUserFields(user, request);

        //Если проверка не пройдена - пользователя не обновляем
        validateLogin(newUser);
        replaceBlankNameToLogin(newUser);

        checkAndInitializeLists(newUser); //инициализация списков, если не установлены
        newUser = userStorage.update(newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    private void replaceBlankNameToLogin(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public List<UserDto> addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        checkAndInitializeLists(user); //инициализация списков, если не установлены
        checkAndInitializeLists(friend);

        Set<Integer> allFriendsId = user.getFriends();

        if (!allFriendsId.contains(friendId)) {
            allFriendsId.add(friendId);
            user.setFriends(allFriendsId);
        }

        allFriendsId = friend.getFriends();

        if (!allFriendsId.contains(userId)) {
            allFriendsId.add(userId);
            friend.setFriends(allFriendsId);
        }

        userStorage.updateFriends(friend);
        userStorage.updateFriends(user);

        return getFriends(userId);

    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    public List<UserDto> deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        checkAndInitializeLists(user); //инициализация списков, если не установлены

        Set<Integer> allFriendsId = user.getFriends();
        if (allFriendsId.contains(friendId)) {
            allFriendsId.remove(friendId);
            user.setFriends(allFriendsId);
        } else {
            String errorMessage = "Пользователь с id "+friendId+" не найден";
            usersLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

//        allFriendsId = friend.getFriends();
//        if (allFriendsId != null && allFriendsId.contains(userId)) {
//            allFriendsId.remove(userId);
//            friend.setFriends(allFriendsId);
//        }

//        List<User> userList = new ArrayList<>();
//        userList.add(user);
//        userList.add(friend);

//        List<UserDto> userList = new ArrayList<>();
//        userList.add(UserMapper.mapToUserDto(user));
//        userList.add(UserMapper.mapToUserDto(friend));
//
//        return userList;

        return getFriends(userId);
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    public List<UserDto> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        checkAndInitializeLists(user); //инициализация списков, если не установлены

        List<UserDto> friends = user.getFriends()
                .stream()
                .map(currentUserId -> userStorage.getUserById(currentUserId))
                .filter(currentUser -> currentUser != null)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        return friends;
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    public List<UserDto> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);

        checkAndInitializeLists(user); //инициализация списков, если не установлены
        checkAndInitializeLists(otherUser); //инициализация списков, если не установлены

        List<UserDto> friends = user.getFriends()
                .stream()
                .filter(currentId -> otherUser.getFriends().contains(currentId))
                .map(currentUserId -> userStorage.getUserById(currentUserId))
                .filter(currentUser -> currentUser != null)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());

        return friends;
    }

    private void validateLogin(User user) {
        if (user.getLogin().contains(" ")) {
            String errorMessage = "Логин не может содержать пробелы";

            usersLog.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void checkAndInitializeLists (User user) {
        if (user.getFriends()==null) {
            user.setFriends(new HashSet<>());
        }

        if (user.getFriendRequests()==null) {
            user.setFriendRequests(new HashSet<>());
        }

    }
}
