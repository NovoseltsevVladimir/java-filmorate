package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    //PUT /users/{id}/friends/{friendId} — добавление в друзья.
    public void addFriend(UserStorage userStorage, int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }

        if (friend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + "не найден");
        }

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

        //return user;
    }

    //DELETE /users/{id}/friends/{friendId} — удаление из друзей.
    public List<User> deleteFriend(UserStorage userStorage, int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }

        if (friend == null) {
            throw new NotFoundException("Пользователь с id " + friendId + "не найден");
        }

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
    public List<User> getFriends(UserStorage userStorage, int userId) {
        User user = userStorage.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }

        if (user.getFriends() == null) {
            return new ArrayList<User>();
        }

        List<User> friends = user.getFriends()
                .stream()
                .map(currentUserId -> userStorage.getUserById(currentUserId))
                .filter(currentUser -> currentUser != null)
                .collect(Collectors.toList());
        return friends;
    }

    //GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем.
    public List<User> getCommonFriends(UserStorage userStorage, int userId, int otherId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }

        if (otherUser == null) {
            throw new NotFoundException("Пользователь с id " + otherId + "не найден");
        }

        Set<Integer> otherFriends = otherUser.getFriends();

        List<User> friends = user.getFriends()
                .stream()
                .filter(currentId -> otherFriends.contains(currentId))
                .map(currentUserId -> userStorage.getUserById(currentUserId))
                .filter(currentUser -> currentUser != null)
                .collect(Collectors.toList());

        return friends;
    }
}
