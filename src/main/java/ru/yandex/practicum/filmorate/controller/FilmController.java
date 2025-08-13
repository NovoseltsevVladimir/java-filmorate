package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.checkers.FilmChecker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private final static Logger filmLog = LoggerFactory.getLogger(Film.class);

    //получение всех фильмов.
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    //добавление фильма;
    @PostMapping
    public Film create(@RequestBody Film film) {

        if (!FilmChecker.checkAndLogFilm(film, filmLog)) {
            throw new ValidationException("Валидация не пройдена");
        }

        int newId = getNextId();
        film.setId(newId);
        films.put(newId, film);

        return film;
    }

    //обновление фильма;
    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        //Если проверка не пройдена - не фильм не обновляем
        if (!FilmChecker.checkAndLogFilm(newFilm, filmLog)) {
            throw new ValidationException("Валидация не пройдена");
        }

        int filmId = newFilm.getId();
        if (!films.containsKey(filmId)) {
            throw new ValidationException("Фильм с id " + filmId + " отсутствует");
        }

        films.put(filmId, newFilm);

        return newFilm;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
