package ru.yandex.practicum.filmorate.mapper;

import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor
public class FilmMapper {

        public static Film mapToFilm(NewFilmRequest request) {

            Film film = new Film();
            film.setName(request.getName());
            film.setDescription(request.getDescription());
            film.setReleaseDate(request.getReleaseDate());
            film.setDuration(request.getDuration());
            film.setMpa(request.getMpa());
            film.setGenres(request.getGenres());

            return film;
        }

        public static FilmDto mapToFilmDto(Film film) {

            FilmDto dto = new FilmDto();
            dto.setId(film.getId());
            dto.setName(film.getName());
            dto.setDescription(film.getDescription());
            dto.setDuration(film.getDuration());
            dto.setReleaseDate(film.getReleaseDate());
            dto.setMpa(film.getMpa());
            dto.setGenres(film.getGenres());
            dto.setUsersIdWithLikes(film.getUsersIdWithLikes());

            return dto;
        }

        public static Film updateFilmFields(Film film, UpdateFilmRequest request) {

            if (request.hasReleaseDate()) {
                film.setReleaseDate(request.getReleaseDate());
            }

            if (request.hasName()) {
                film.setName(request.getName());
            }

            if (request.hasDescription()) {
                film.setDescription(request.getDescription());
            }

            if (request.hasDuration()) {
                film.setDuration(request.getDuration());
            }

            if (request.hasMpa()) {
                film.setMpa(request.getMpa());
            }

            return film;
        }
    }