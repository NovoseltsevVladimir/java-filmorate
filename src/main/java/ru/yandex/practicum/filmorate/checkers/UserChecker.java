package ru.yandex.practicum.filmorate.checkers;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserChecker {

    public static boolean checkAndLogUser(User user, Logger log) {
        boolean result = true;

        if (!emailIsCorrect(user)) {
            result = false;
            log.warn("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (!loginIsCorrect(user)) {
            result = false;
            log.warn("Логин не может быть пустым и содержать пробелы");
        }

        if (!birthdayIsCorrect(user)) {
            result = false;
            log.warn("Дата рождения не может быть в будущем");
        }

        return result;
    }

    //Электронная почта не может быть пустой и должна содержать символ @;
    private static boolean emailIsCorrect(User user) {
        String email = user.getEmail();
        return !(email == null || email.isBlank() || !email.contains("@"));
    }

    //логин не может быть пустым и содержать пробелы;
    private static boolean loginIsCorrect(User user) {
        String login = user.getLogin();
        return !(login == null || login.isBlank() || login.contains(" "));
    }

    //дата рождения не может быть в будущем
    private static boolean birthdayIsCorrect(User user) {
        LocalDate birthday = user.getBirthday();
        return !(birthday.isAfter(LocalDate.now()));
    }

}

