package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;
    private final Logger filmLog;

    public InMemoryFilmStorage() {
        this.films = new HashMap<Integer, Film>();
        this.filmLog = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {

        int newId = getNextId();
        film.setId(newId);
        films.put(newId, film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {

        int filmId = newFilm.getId();
        if (!films.containsKey(filmId)) {
            String errorMessage = "Фильм с id " + filmId + " отсутствует";

            filmLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        films.put(filmId, newFilm);

        return newFilm;
    }

    @Override
    public void remove(Film film) {
        Integer filmId = film.getId();
        if (!films.containsKey(filmId)) {
            String errorMessage = "Фильм с id " + filmId + " отсутствует";

            filmLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            films.remove(filmId);
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

    @Override
    public Film getFilmById(Integer id) {
        Film film = films.get(id);
        if (film == null) {
            String errorMessage = "Фильм с id " + id + " отсутствует";

            filmLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }
        return film;
    }

    @Override
    public List<Integer> getFilmGenreId(Film film) {
        return List.of();
    }
}
