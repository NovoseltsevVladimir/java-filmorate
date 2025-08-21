package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public Collection<User> findAll();

    public User create(User user);

    public User update(User user);

    public boolean remove(User user);

    public int getNextId();

    public void removeAll();

    public void validateLogin (User user);
}
