package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

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
    private final Logger filmLog = LoggerFactory.getLogger(this.getClass());

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        validateReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        //Если проверка не пройдена - фильм не обновляем
        validateReleaseDate(newFilm);
        return filmStorage.update(newFilm);
    }

    private void validateReleaseDate(Film film) {

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            String errorMessage = "Дата релиза фильма должна быть больше 28 декабря 1895 года";

            filmLog.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    //    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        //Проверить есть ли в хранилище пользователь, отдельная переменная не нужна
        userStorage.getUserById(userId);

        Set<Integer> allLikesId = film.getUsersIdWithLikes();
        if (allLikesId == null) {
            allLikesId = new HashSet<>();
            allLikesId.add(userId);
            film.setUsersIdWithLikes(allLikesId);
        } else if (!allLikesId.contains(userId)) {
            allLikesId.add(userId);
            film.setUsersIdWithLikes(allLikesId);
        }
    }

    //    DELETE /films/{id}/like/{userId} — пользователь удаляет лайк.
    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        //Проверить есть ли в хранилище пользователь, отдельная переменная не нужна
        userStorage.getUserById(userId);

        Set<Integer> allLikesId = film.getUsersIdWithLikes();
        if (allLikesId == null || !allLikesId.contains(userId)) {
            String errorMessage = "Пользователь с id " + userId + "не ставил лайк фильму с id" + filmId;

            filmLog.warn(errorMessage);
            throw new NotFoundException(errorMessage);
        } else {
            allLikesId.remove(userId);
            film.setUsersIdWithLikes(allLikesId);
        }
    }

    //    GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    //    Если значение параметра count не задано, верните первые 10.
    public List<Film> getPopularFilms(Integer count) {

        Comparator<Film> filmComparator = (film1, film2) -> {
            int o1 = (film1.getUsersIdWithLikes() == null) ? 0 : film1.getUsersIdWithLikes().size();
            int o2 = (film2.getUsersIdWithLikes() == null) ? 0 : film2.getUsersIdWithLikes().size();

            return o2 - o1;
        };

        Collection<Film> filmsList = filmStorage.findAll();

        if (filmsList == null) {
            return new ArrayList<Film>();
        }

        if (count > filmsList.size()) {
            count = filmsList.size();
        }

        List<Film> popularFilms = filmsList
                .stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());

        return popularFilms;
    }
}
