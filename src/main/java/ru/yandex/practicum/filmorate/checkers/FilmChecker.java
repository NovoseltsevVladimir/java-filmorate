package ru.yandex.practicum.filmorate.checkers;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmChecker {
    public static boolean checkAndLogFilm(Film film, Logger log) {
        boolean result = true;

        if (!nameIsCorrect(film)) {
            result = false;
            log.warn("Наименование не должно быть пустым");
        }

        if (!descriptionIsCorrect(film)) {
            result = false;
            log.warn("Описание должно быть не более 200 символов");
        }

        if (!releaseDateIsCorrect(film)) {
            result = false;
            log.warn("Дата релиза фильма должна быть больше 28 декабря 1895 года");
        }

        if (!durationIsCorrect(film)) {
            result = false;
            log.warn("Некорректная продолжительность фильма. Продолжительность должна быть положительной");
        }

        return result;
    }

    //название не может быть пустым
    private static boolean nameIsCorrect(Film film) {
        String name = film.getName();
        return !(name == null || name.isBlank());
    }

    //максимальная длина описания — 200 символов
    private static boolean descriptionIsCorrect(Film film) {
        String description = film.getDescription();
        return !(description == null || description.length() > 200);
    }

    //дата релиза — не раньше 28 декабря 1895 года
    private static boolean releaseDateIsCorrect(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        return !(releaseDate == null || releaseDate.isBefore(LocalDate.of(1895, 12, 28)));
    }

    //продолжительность фильма должна быть положительным числом
    private static boolean durationIsCorrect(Film film) {
        Integer duration = film.getDuration();
        return !(duration == null || !(duration > 0));
    }
}
