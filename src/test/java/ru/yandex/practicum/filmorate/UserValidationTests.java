package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserValidationTests {

    private static Validator validator;
    private User user;

    @BeforeAll
    static void clearFilms() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void fillFilm() {
        user = new User();
        user.setBirthday(LocalDate.now());
        user.setEmail("tututu@mail.com");
        user.setLogin("User");
        user.setName("User ru");
    }

    @Test
    void testEmailValidation() {

        user.setEmail(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Поле Email, валидация NULL не пройдена");

        user.setEmail("");

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Поле Email, валидация Blank не пройдена");

        user.setEmail("@3.ru");

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Поле Email, валидация EmailFormat не пройдена");

    }

    @Test
    void testLoginValidation() {

        user.setLogin(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Поле Login, валидация NULL не пройдена");

        user.setLogin("");

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Поле Login, валидация Blank не пройдена");

        user.setLogin("Lo g in");
        UserController userController = new UserController();

        try {
            userController.validateLogin(user);
            assertFalse(true, "Поле Login, валидация NoSpaces не пройдена");
        } catch (ValidationException exp) {
            //Если ошибка возникла, то все ок
        }
    }

    @Test
    void testBirthdayValidation() {

        user.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Поле Birthday, валидация NULL не пройдена");

        user.setBirthday(LocalDate.now().plusDays(1));

        violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Поле Login, валидация PastOrPresent не пройдена");
    }
}
