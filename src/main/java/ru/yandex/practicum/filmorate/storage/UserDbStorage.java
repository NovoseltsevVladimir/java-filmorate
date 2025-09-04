package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

@Component()
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage{

    @Override
    public Collection<User> findAll() {
        return List.of();
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void remove(User user) {

    }

    @Override
    public User getUserById(Integer id) {
        return null;
    }
}
