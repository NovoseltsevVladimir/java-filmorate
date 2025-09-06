package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component()
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage{

    private final FilmRepository repository;
    private final Logger log;

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
        Optional<Film> oldFilm = repository.findById(filmId);

        if (oldFilm.isEmpty()) {
            String errorMessage = "Фильм с id " + filmId + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        repository.update(film);

        //Лайки
        return film;
    }

    @Override
    public void remove(Film film) {
        int filmId = film.getId();
        Optional<Film> optionalFilm = repository.findById(filmId);

        if (optionalFilm.isEmpty()) {
            String errorMessage = "Фильм с id " + filmId + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        repository.delete(filmId);
    }

    @Override
    public Film getFilmById(Integer id) {
        Optional<Film> optionalFilm = repository.findById(id);
        if (optionalFilm.isEmpty()) {
            String errorMessage = "Фильм с id " + id + " отсутствует";
            log.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            return optionalFilm.get();
        }
    }

    @Override
    public List<Integer> getFilmGenreId(Film film) {
        return repository.getFilmGenreId(film);
    }

}
