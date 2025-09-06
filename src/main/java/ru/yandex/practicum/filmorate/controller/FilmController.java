package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    //получение всех фильмов.
    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    //добавление фильма;
    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest film) {
        return filmService.create(film);
    }

    //обновление фильма;
    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest newFilm) {
        return filmService.update(newFilm);
    }

    //    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    @PutMapping("/{id}/like/{userId}")
    public void putNewLike(@PathVariable("id") int filmId,
                           @PathVariable("userId") int userId) {

        filmService.addLike(filmId, userId);
    }

    //    DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId,
                           @PathVariable("userId") int userId) {

        filmService.deleteLike(filmId, userId);
    }

    //    GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    //    Если значение параметра count не задано, верните первые 10.
    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(name = "count", defaultValue = "10", required = false) int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable("id") int filmId) {

        return filmService.getById(filmId);
    }
}
