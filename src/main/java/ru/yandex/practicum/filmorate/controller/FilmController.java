package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private final Logger filmLog = LoggerFactory.getLogger(Film.class);

    //получение всех фильмов.
    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    //добавление фильма;
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {

        validateReleaseDate(film);

        int newId = getNextId();
        film.setId(newId);
        films.put(newId, film);

        return film;
    }

    //обновление фильма;
    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        //Если проверка не пройдена - фильм не обновляем
        validateReleaseDate(newFilm);

        int filmId = newFilm.getId();
        if (!films.containsKey(filmId)) {
            throw new ValidationException("Фильм с id " + filmId + " отсутствует");
        }

        films.put(filmId, newFilm);

        return newFilm;
    }

    private void validateReleaseDate(Film film) {

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            filmLog.warn("Дата релиза фильма должна быть больше 28 декабря 1895 года");
            throw new ValidationException("Валидация не пройдена");
        }


    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    public void clearFilms() {
        films.clear();
    }
}
