package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.checkers.FilmChecker;
import ru.yandex.practicum.filmorate.checkers.UserChecker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void contextLoads() {

        Film film = new Film();
        film.setName("");
        film.setDescription("Фильм обо всем");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        Logger log = LoggerFactory.getLogger(Film.class);

        Assertions.assertFalse(FilmChecker.checkAndLogFilm(film, log), "Некорректная валидация наименования");

        film.setName("Лалалал");
        String incorrectDescription = "";
        for (int i = 0; i < 201; i++) {
            incorrectDescription += "A";
        }

        film.setDescription(incorrectDescription);
        Assertions.assertFalse(FilmChecker.checkAndLogFilm(film, log), "Некорректная валидация описания");

        film.setReleaseDate(LocalDate.of(1500, 1, 1));
        Assertions.assertFalse(FilmChecker.checkAndLogFilm(film, log), "Некорректная валидация даты релиза");

        film.setDuration(-500);
        Assertions.assertFalse(FilmChecker.checkAndLogFilm(film, log), "Некорректная валидация продолжительности");

        Logger userlog = LoggerFactory.getLogger(Film.class);
        User user = new User();
        user.setBirthday(LocalDate.now());
        user.setEmail("lalala.com");
        user.setLogin("User");
        Assertions.assertFalse(UserChecker.checkAndLogUser(user, userlog), "Некорректная валидация эл. почты");

        user.setLogin("tu tu");
        Assertions.assertFalse(UserChecker.checkAndLogUser(user, userlog), "Некорректная валидация логина");

        user.setBirthday(LocalDate.now().plusDays(1));
        Assertions.assertFalse(UserChecker.checkAndLogUser(user, userlog), "Некорректная валидация даты рождения");

    }

}
