package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
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
                .map(film -> initializeGenres(film))
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getById (int id) {
        Film film = filmStorage.getFilmById(id);
        checkAndInitializeLists(film);
        initializeGenres(film);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto create(NewFilmRequest request) {
        Film film = FilmMapper.mapToFilm(request);

        validateReleaseDate(film);
        validateAndFillMpa(film);
        validateAndFillGenres(film);
        checkAndInitializeLists (film);
        film = filmStorage.create(film);

        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest request) {

        Film film = filmStorage.getFilmById(request.getId());
        Film newFilm  = FilmMapper.updateFilmFields(film,request);

        //Если проверка не пройдена - фильм не обновляем
        validateReleaseDate(newFilm);
        validateAndFillMpa(newFilm);
        validateAndFillGenres(film);
        checkAndInitializeLists (newFilm);
        newFilm = filmStorage.update(newFilm);
        newFilm = initializeGenres(newFilm);

        return FilmMapper.mapToFilmDto(newFilm);
    }

    private void validateReleaseDate(Film film) {

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String errorMessage = "Дата релиза фильма должна быть больше 28 декабря 1895 года";

            filmLog.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validateAndFillMpa(Film film) {

        Rating mpa = film.getMpa();
        if (mpa != null) {
            mpa = mpaStorage.getById(mpa.getId());
            film.setMpa(mpa);
        }
    }

    private void validateAndFillGenres(Film film) {

        Set <Genre> genres = film.getGenres();
        if (genres != null) {
            Set <Genre> newGenres = new HashSet<>();
            for (Genre genre:genres) {
                Genre genreForAdd = genreStorage.getById(genre.getId());
                newGenres.add(genreForAdd);
            }
        }
    }

    //    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        //Проверить есть ли в хранилище пользователь, отдельная переменная не нужна
        userStorage.getUserById(userId);
        checkAndInitializeLists (film);
        Set<Integer> allLikesId = film.getUsersIdWithLikes();

        if (!allLikesId.contains(userId)) {
            allLikesId.add(userId);
            film.setUsersIdWithLikes(allLikesId);
        }
    }

    //    DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        //Проверить есть ли в хранилище пользователь, отдельная переменная не нужна
        userStorage.getUserById(userId);
        checkAndInitializeLists (film);

        Set<Integer> allLikesId = film.getUsersIdWithLikes();
        if (allLikesId.contains(userId)) {
            allLikesId.remove(userId);
            film.setUsersIdWithLikes(allLikesId);
        }
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
                .sorted(filmComparator)
                .limit(count)
                .map(film -> initializeGenres(film))
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());

        return popularFilms;
    }

    private void checkAndInitializeLists (Film film) {
        if (film.getUsersIdWithLikes()==null) {
            film.setUsersIdWithLikes(new HashSet<>());
        }

        if (film.getGenres()==null) {
            film.setGenres(new HashSet<>());
        }
    }

    private Film initializeGenres (Film film) {
       List <Genre> genres = filmStorage.getFilmGenreId(film)
                .stream()
                .map(genreId -> genreStorage.getById(genreId))
                .collect(Collectors.toList());

       film.setGenres(new HashSet<>(genres));

       return film;
    }

}
