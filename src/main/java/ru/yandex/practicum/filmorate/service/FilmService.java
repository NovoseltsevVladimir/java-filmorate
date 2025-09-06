package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Autowired
    @Qualifier("FilmDbStorage")
    private final FilmStorage filmStorage = null;
    @Autowired
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage = null;
    @Autowired
    private final GenreDbStorage genreStorage = null;
    @Autowired
    private final RatingDbStorage mpaStorage = null;

    private final Logger filmLog = LoggerFactory.getLogger(this.getClass());

    public Collection<FilmDto> findAll() {

        return filmStorage.findAll()
                .stream()
                .map(film -> fill(film))
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById(int id) {
        Film film = filmStorage.getFilmById(id);
        checkAndInitializeLists(film);
        film = fill(film);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto create(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);
        film = validate(film);

        film = filmStorage.create(film);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {

        Film film = filmStorage.getFilmById(request.getId());
        Film newFilm = FilmMapper.updateFilmFields(film, request);

        //Если проверка не пройдена - фильм не обновляем
        validate(film);
        newFilm = filmStorage.update(newFilm);

        return FilmMapper.mapToFilmDto(newFilm);
    }

    private void validateReleaseDate(Film film) {

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String errorMessage = "Дата релиза фильма должна быть больше 28 декабря 1895 года";

            filmLog.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    //    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    public FilmDto addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        //Проверить есть ли в хранилище пользователь, отдельная переменная не нужна
        userStorage.getUserById(userId);
        checkAndInitializeLists(film);
        film = fill(film);
        Set<Integer> allLikesId = film.getUsersIdWithLikes();

        if (!allLikesId.contains(userId)) {
            allLikesId.add(userId);
            film.setUsersIdWithLikes(allLikesId);
        }

        filmStorage.update(film);

        return FilmMapper.mapToFilmDto(film);
    }

    //    DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    public FilmDto deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        //Проверить есть ли в хранилище пользователь, отдельная переменная не нужна
        userStorage.getUserById(userId);
        checkAndInitializeLists(film);
        film = fill(film);
        Set<Integer> allLikesId = film.getUsersIdWithLikes();
        if (allLikesId.contains(userId)) {
            allLikesId.remove(userId);
            film.setUsersIdWithLikes(allLikesId);
        }

        filmStorage.update(film);
        return FilmMapper.mapToFilmDto(film);
    }

    //    GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    //    Если значение параметра count не задано, верните первые 10.
    public List<FilmDto> getPopularFilms(Integer count) {

        Comparator<Film> filmComparator = (film1, film2) -> {
            int o1 = (film1.getUsersIdWithLikes() == null) ? 0 : film1.getUsersIdWithLikes().size();
            int o2 = (film2.getUsersIdWithLikes() == null) ? 0 : film2.getUsersIdWithLikes().size();

            return o2 - o1;
        };

        Collection<Film> filmsList = filmStorage.findAll();

        if (filmsList == null) {
            return new ArrayList<FilmDto>();
        }

        if (count > filmsList.size()) {
            count = filmsList.size();
        }

        List<FilmDto> popularFilms = filmsList
                .stream()
                .map(film -> fill(film))
                .sorted(filmComparator)
                .map(FilmMapper::mapToFilmDto)
                .limit(count)
                .collect(Collectors.toList());

        return popularFilms;
    }

    private void checkAndInitializeLists(Film film) {
        if (film.getUsersIdWithLikes() == null) {
            film.setUsersIdWithLikes(new HashSet<>());
        }

        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
    }

    private Film validate(Film film) {

        checkAndInitializeLists(film);
        validateReleaseDate(film);
        validateGenres(film);
        validateMpa(film);

        return film;
    }

    private Film fill(Film film) {
        film = fillGenres(film);
        film = fillMpa(film);
        film = fillLikes(film);

        return film;
    }

    private void validateGenres(Film film) {
        for (Genre genre : film.getGenres()) {
            genreStorage.getById(genre.getId()); //если не найдено - будет ошибка
        }
    }

    private void validateMpa(Film film) {
        Rating mpa = film.getMpa();
        if (mpa != null) {
            mpaStorage.getById(mpa.getId()); //если не найдено - будет ошибка
        }
    }

    private Film fillGenres(Film film) {
        List<Genre> genreList = filmStorage.getFilmGenreId(film)
                .stream()
                .map(id -> genreStorage.getById(id))
                .collect(Collectors.toList());
        film.setGenres(new HashSet<>(genreList));

        return film;
    }

    private Film fillMpa(Film film) {

        Rating mpa = filmStorage.getFilmMpa(film);
        film.setMpa(mpa);
        return film;
    }

    private Film fillLikes(Film film) {
        film.setUsersIdWithLikes(new HashSet<>(filmStorage.getFilmLikes(film)));
        return film;
    }

}
