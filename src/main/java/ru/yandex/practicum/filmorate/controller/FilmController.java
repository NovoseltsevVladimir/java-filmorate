package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.Collection;

@RestController
@RequestMapping("/films")
public class FilmController {

   private FilmStorage filmStorage = new InMemoryFilmStorage();

    //получение всех фильмов.
    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    //добавление фильма;
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    //обновление фильма;
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void clearFilms() {
        filmStorage.removeAll();
    }

    public void validateReleaseDate (Film film) {
        filmStorage.validateReleaseDate(film);
    }
}
