package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films;
    private final Logger filmLog;

    public InMemoryFilmStorage() {
        this.films = new HashMap<>();
        this.filmLog = LoggerFactory.getLogger(Film.class);
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        validateReleaseDate(film);

        int newId = getNextId();
        film.setId(newId);
        films.put(newId, film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        //Если проверка не пройдена - фильм не обновляем
        validateReleaseDate(newFilm);

        int filmId = newFilm.getId();
        if (!films.containsKey(filmId)) {
            throw new ValidationException("Фильм с id " + filmId + " отсутствует");
        }

        films.put(filmId, newFilm);

        return newFilm;
    }

    @Override
    public boolean remove(Film film) {
        return false;
    }

    @Override
    public int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

    @Override
    public void validateReleaseDate(Film film) {

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            filmLog.warn("Дата релиза фильма должна быть больше 28 декабря 1895 года");
            throw new ValidationException("Валидация не пройдена");
        }
    }

    public void removeAll() {
        films.clear();
    }
}
