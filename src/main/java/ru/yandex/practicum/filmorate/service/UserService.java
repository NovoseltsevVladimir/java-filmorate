package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    //добавление пользователя;
    public User create(User user) {
        validateLogin(user);
        replaceBlankNameToLogin(user);

        return userStorage.create(user);
    }

    //обновление пользователя;
    public User update(User newUser) {
        //Если проверка не пройдена - пользователя не обновляем
        validateLogin(newUser);
        replaceBlankNameToLogin(newUser);

        return userStorage.update(newUser);
    }

    private void replaceBlankNameToLogin(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Integer> allFriendsId = user.getFriends();

        if (allFriendsId == null) {
            allFriendsId = new HashSet<>();
            allFriendsId.add(friendId);
            user.setFriends(allFriendsId);
        } else if (!allFriendsId.contains(friendId)) {
            allFriendsId.add(friendId);
            user.setFriends(allFriendsId);
        }

        allFriendsId = friend.getFriends();

        if (allFriendsId == null) {
            allFriendsId = new HashSet<>();
            allFriendsId.add(userId);
            friend.setFriends(allFriendsId);
        } else if (!allFriendsId.contains(userId)) {
            allFriendsId.add(userId);
            friend.setFriends(allFriendsId);
        }

    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    public List<User> deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Integer> allFriendsId = user.getFriends();
        if (allFriendsId != null && allFriendsId.contains(friendId)) {
            allFriendsId.remove(friendId);
            user.setFriends(allFriendsId);
        }

        allFriendsId = friend.getFriends();
        if (allFriendsId != null && allFriendsId.contains(userId)) {
            allFriendsId.remove(userId);
            friend.setFriends(allFriendsId);
        }

        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(friend);

        return userList;
    }

    //GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями.
    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId);

        Collection<Integer> userList = user.getFriends();

        if (userList == null) {
            return new ArrayList<User>();
        }

        List<User> friends = userList
                .stream()
                .map(currentUserId -> userStorage.getUserById(currentUserId))
                .filter(currentUser -> currentUser != null)
                .collect(Collectors.toList());
        return friends;
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);

        Set<Integer> thisFriends = user.getFriends();
        Set<Integer> otherFriends = otherUser.getFriends();
        if (otherFriends == null || thisFriends == null) {
            return new ArrayList<User>();
        }

        List<User> friends = thisFriends
                .stream()
                .filter(currentId -> otherFriends.contains(currentId))
                .map(currentUserId -> userStorage.getUserById(currentUserId))
                .filter(currentUser -> currentUser != null)
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
}
