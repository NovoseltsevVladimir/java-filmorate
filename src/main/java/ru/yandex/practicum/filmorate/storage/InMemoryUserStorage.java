package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Logger usersLog = LoggerFactory.getLogger(User.class);

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        validateLogin(user);

        int newId = getNextId();
        user.setId(newId);
        replaceBlankNameToLogin(user);
        users.put(newId, user);

        return user;
    }

    @Override
    public User update(User newUser) {
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

    @Override
    public boolean remove(User user) {
        return false;
    }

    @Override
    public int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @Override
    public void removeAll() {
        users.clear();
    }


    private void replaceBlankNameToLogin(User user) {
        String name = user.getName();
        if (name == null || name.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void validateLogin(User user) {
        if (user.getLogin().contains(" ")) {
            usersLog.warn("Логин не может содержать пробелы");
            throw new ValidationException("Валидация не пройдена");
        }
    }
}
