package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

public class FilmControllerTests {

    private FilmController filmController;

    public FilmControllerTests() {
        this.filmController = new FilmController(new InMemoryFilmStorage(), new InMemoryUserStorage());
    }

    @AfterEach
    void clearFilms() {
        filmController.clearFilms();
    }

    @Test
    void testAddFilm() {

        Film film = new Film();
        film.setName("New");
        film.setDescription("Фильм обо всем");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        filmController.create(film);

        Assertions.assertEquals(1, filmController.findAll().size(), "Неверное количество фильмов");
    }

    @Test
    void testUpdateFilm() {

        Film film = new Film();
        film.setName("New");
        film.setDescription("Фильм обо всем");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());

        filmController.create(film);

        Assertions.assertEquals(1, filmController.findAll().size(), "Неверное количество фильмов");

        Film newFilm = new Film();
        newFilm.setName("Old");
        newFilm.setDescription("Фильм ни о чем");
        newFilm.setDuration(130);
        newFilm.setReleaseDate(LocalDate.now().plusDays(1));
        newFilm.setId(film.getId());

        filmController.update(newFilm);
        Collection<Film> films = filmController.findAll();
        Assertions.assertEquals(1, films.size(), "Неверное количество фильмов");

        Film filmInCollection = films.stream().findFirst().get();
        Assertions.assertEquals(newFilm.getName(), filmInCollection.getName(),
                "Не перезаписывается name");
        Assertions.assertEquals(newFilm.getDescription(), filmInCollection.getDescription(),
                "Не перезаписывается description(");
        Assertions.assertEquals(newFilm.getDuration(), filmInCollection.getDuration(),
                "Не перезаписывается duration");
        Assertions.assertEquals(newFilm.getReleaseDate(), filmInCollection.getReleaseDate(),
                "Не перезаписывается releaseDate");
    }

    @Test
    void testIncorrectReleaseDate() {

        Film film = new Film();
        film.setName("New");
        film.setDescription("Фильм обо всем");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(1500, 1, 1));

        try {
            filmController.create(film);
            Assertions.assertEquals(0, 1, "Отсутствует ошибка валидации");
        } catch (ValidationException exp) {
            Assertions.assertEquals(0, filmController.findAll().size());
        }
    }
}
