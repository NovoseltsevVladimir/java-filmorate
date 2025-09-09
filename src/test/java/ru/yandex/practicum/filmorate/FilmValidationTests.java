package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class FilmValidationTests {

    private static Validator validator;
    private Film film;
    private FilmService filmService;

    public FilmValidationTests() {
        this.filmService = new FilmService();
    }

    @BeforeAll
    static void clearFilms() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void fillFilm() {
        film = new Film();
        film.setName("New");
        film.setDescription("Фильм обо всем");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());
    }

    @Test
    void testNameValidation() {

        film.setName(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Поле Name, валидация NULL не пройдена");

        film.setName("");

        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Поле Name, валидация Blank не пройдена");

    }

    @Test
    void testDescriptionValidation() {
        film.setDescription(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Поле Description, валидация NULL не пройдена");

        String maxDescription = "";
        for (int i = 0; i < 201; i++) {
            maxDescription += "a";
        }
        film.setDescription(maxDescription);

        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Поле Description, валидация MaxLength=200 не пройдена");
    }

    @Test
    void testReleaseDateValidation() {
        film.setReleaseDate(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Поле ReleaseDate, валидация NULL не пройдена");

        film.setReleaseDate(LocalDate.of(1500, 1, 1));

        NewFilmRequest newFilm = new NewFilmRequest();
        newFilm.setName(film.getName());
        newFilm.setDuration(film.getDuration());
        newFilm.setReleaseDate(film.getReleaseDate());
        newFilm.setDescription(film.getDescription());
        newFilm.setMpa(film.getMpa());

        try {
            filmService.create(newFilm);
            assertFalse(true, "Поле ReleaseDate, валидация 1895.12.28 не пройдена");
        } catch (ValidationException ignored) {
            //Если ошибка возникла, то все ок
        }
    }

    @Test
    void testDurationValidation() {

        film.setDuration(null);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Поле Duration, валидация NULL не пройдена");

        film.setDuration(-5);

        violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Поле Duration, валидация Positive не пройдена");

    }
}
