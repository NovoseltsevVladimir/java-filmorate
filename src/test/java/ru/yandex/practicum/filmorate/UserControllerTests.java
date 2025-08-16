package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

public class UserControllerTests {
    UserController userController = new UserController();

    @AfterEach
    void clearUsers() {
        userController.clearUsers();
    }

    @Test
    void testAddFilm() {
        User user = new User();
        user.setBirthday(LocalDate.now());
        user.setEmail("lalala@mail.com");
        user.setLogin("User");

        userController.create(user);

        Assertions.assertEquals(1, userController.findAll().size(), "Неверное количество пользователей");
    }

    @Test
    void testIncorrectLogin() {

        User user = new User();
        user.setBirthday(LocalDate.now());
        user.setEmail("lalala@mail.com");
        user.setLogin("User ru");

        try {
            userController.create(user);
            Assertions.assertEquals(0, 1, "Отсутствует ошибка валидации");
        } catch (ValidationException exp) {
            Assertions.assertEquals(0, userController.findAll().size());
        }

    }

    @Test
    void testFillName() {

        User user = new User();
        user.setBirthday(LocalDate.now());
        user.setEmail("lalala@mail.com");
        user.setLogin("User");

        userController.create(user);
        Assertions.assertEquals(user.getLogin(), user.getName(),
                "При отсутствии имени логин в поле имя не записывается");
    }

    @Test
    void testUpdateUser() {

        User user = new User();
        user.setBirthday(LocalDate.now());
        user.setEmail("lalala@mail.com");
        user.setLogin("User");
        user.setName("User ru");

        userController.create(user);

        Assertions.assertEquals(1, userController.findAll().size(),
                "Неверное количество пользователей");

        User newUser = new User();
        newUser.setBirthday(LocalDate.now());
        newUser.setEmail("tututu@mail.com");
        newUser.setLogin("User1");
        newUser.setName("User 1 ru");
        newUser.setId(user.getId());

        userController.update(newUser);
        Collection<User> users = userController.findAll();
        Assertions.assertEquals(1, users.size(), "Неверное количество польователей");

        User userInCollection = users.stream().findFirst().get();

        Assertions.assertEquals(newUser.getBirthday(), userInCollection.getBirthday(),
                "Не перезаписывается Birthday");
        Assertions.assertEquals(newUser.getEmail(), userInCollection.getEmail(),
                "Не перезаписывается Email");
        Assertions.assertEquals(newUser.getLogin(), userInCollection.getLogin(),
                "Не перезаписывается Login");
        Assertions.assertEquals(newUser.getName(), userInCollection.getName(),
                "Не перезаписывается name");
    }

}
