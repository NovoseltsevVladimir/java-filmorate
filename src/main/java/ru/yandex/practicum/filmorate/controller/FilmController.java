package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmStorage filmStorage;
    private FilmService filmService = new FilmService();
    private UserStorage userStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage,
                          //FilmService filmService,
                          UserStorage userStorage) {
        this.filmStorage = filmStorage;
        //this.filmService = filmService;
        this.userStorage = userStorage;
    }

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

    public void validateReleaseDate(Film film) {
        filmStorage.validateReleaseDate(film);
    }

    //    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    @PutMapping("/{id}/like/{userId}")
    public void putNewLike(@PathVariable("id") int filmId,
                           @PathVariable("userId") int userId) {

        filmService.addLike(filmStorage, userStorage, filmId, userId);
    }

    //    DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId,
                           @PathVariable("userId") int userId) {

        filmService.deleteLike(filmStorage, userStorage, filmId, userId);
    }

    //    GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    //    Если значение параметра count не задано, верните первые 10.
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10", required = false) int count) {
        return filmService.getPopularFilms(filmStorage, count);
    }
}
