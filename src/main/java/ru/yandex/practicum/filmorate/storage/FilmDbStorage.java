package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository repository;
    private final Logger log;

    @Autowired
    public FilmDbStorage(FilmRepository repository) {
        this.repository = repository;
        this.log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public Collection<Film> findAll() {
        return repository.findAll();
    }

    @Override
    public Film create(Film film) {
        return repository.save(film);
    }

    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        Film oldFilm = repository.findById(filmId);

        if (oldFilm == null) {
            String errorMessage = "Фильм с id " + filmId + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        repository.update(film);

        return film;
    }

    @Override
    public void remove(Film film) {
        int filmId = film.getId();
        Film filmForDelete = repository.findById(filmId);

        if (filmForDelete == null) {
            String errorMessage = "Фильм с id " + filmId + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        repository.delete(filmId);
    }

    @Override
    public Film getFilmById(Integer id) {
        Film film = repository.findById(id);
        if (film == null) {
            String errorMessage = "Фильм с id " + id + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            return film;
        }
    }
}
