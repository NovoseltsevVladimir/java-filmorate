package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    //    PUT /films/{id}/like/{userId} — пользователь ставит лайк фильму.
    public void addLike(FilmStorage filmStorage, UserStorage userStorage, int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }

        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + "не найден");
        }

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
    public void deleteLike(FilmStorage filmStorage, UserStorage userStorage, int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id " + userId + "не найден");
        }

        if (film == null) {
            throw new NotFoundException("Фильм с id " + filmId + "не найден");
        }

        Set<Integer> allLikesId = film.getUsersIdWithLikes();
        if (allLikesId == null || !allLikesId.contains(userId)) {
            throw new NotFoundException("Пользователь с id " + userId
                    + "не ставил лайк фильму с id" + filmId);
        } else {
            allLikesId.remove(userId);
            film.setUsersIdWithLikes(allLikesId);
        }
    }

    //    GET /films/popular?count={count} — возвращает список из первых count фильмов по количеству лайков.
    //    Если значение параметра count не задано, верните первые 10.
    public List<Film> getPopularFilms(FilmStorage filmStorage, Integer count) {

        Comparator<Film> filmComparator = (film1, film2) -> {
            int o1 = (film1.getUsersIdWithLikes() == null) ? 0 : film1.getUsersIdWithLikes().size();
            int o2 = (film2.getUsersIdWithLikes() == null) ? 0 : film2.getUsersIdWithLikes().size();

            return o2 - o1;
        };

        Collection<Film> filmsList = filmStorage.findAll();
        if (count > filmsList.size()) {
            count = filmsList.size();
        }

        List<Film> popularFilms = filmStorage.findAll()
                .stream()
                .sorted(filmComparator)
                .limit(count)
                .collect(Collectors.toList());

        return popularFilms;
    }


}
