package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    public Collection<Film> findAll();

    public Film create(Film film);

    public Film update(Film film);

    public void remove(Film film);

    public Film getFilmById(Integer id);

    public List<Integer> getFilmGenreId(Film film);
}
