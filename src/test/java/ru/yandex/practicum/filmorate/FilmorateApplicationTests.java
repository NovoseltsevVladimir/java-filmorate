package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRepository.class, FilmRowMapper.class,
        UserDbStorage.class, UserRepository.class, UserRowMapper.class,})
class FilmorateApplicationTests {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private Film film;
    private User user;

    @BeforeEach
    void fillFilm() {
        film = new Film();
        film.setName("Film");
        film.setDescription("About");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.now());
        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        genres.add(genre);

        film.setGenres(genres);
        film.setUsersIdWithLikes(new HashSet<>());
    }

    @BeforeEach
    void fillUser() {
        user = new User();
        user.setEmail("lalala@mail.ru");
        user.setLogin("lalala");
        user.setLogin("Name");
        user.setBirthday(LocalDate.now().minusYears(5));
        user.setFriends(new HashSet<>());
    }

    @Test
    public void testCreateAndFindFilmById() {

        film = filmStorage.create(film);

        Assertions.assertEquals("Film", film.getName(), "Наименование не сохранилось");
        Assertions.assertEquals("About", film.getDescription(), "Описание не сохранилось");
        Assertions.assertNotEquals(0, film.getId(), "Ид не присвоилось");

        Film fromStorage = filmStorage.getFilmById(film.getId());
        Assertions.assertEquals(film.getName(), fromStorage.getName(), "Это разные фильмы");
        Assertions.assertEquals(film.getDescription(), fromStorage.getDescription(), "Это разные фильмы");

        Assertions.assertEquals(1, filmStorage.findAll().size(), "Количество фильмов не совпадает");
    }

    @Test
    public void testFilmUpdate() {
        film = filmStorage.create(film);

        Assertions.assertEquals(film.getName(), "Film", "Это разные фильмы");
        film.setName("Фильм");
        film = filmStorage.update(film);
        Assertions.assertEquals(film.getName(), "Фильм", "Это разные фильмы");
    }

    @Test
    public void testGetFilmGenre() {
        film = filmStorage.create(film);
        film = filmStorage.getFilmById(film.getId());

        List<Genre> genres = film.getGenres()
                .stream()
                .collect(Collectors.toList());
        Assertions.assertEquals(genres.size(), 1, "Жанры не сохраняются");
        Assertions.assertEquals(genres.get(0).getId(), 1, "Жанры не сохраняются");
    }

    @Test
    public void testRemoveFilm() {
        film = filmStorage.create(film);
        filmStorage.remove(film);

        Assertions.assertEquals(filmStorage.findAll().size(), 0, "Фильм не удаляется");
    }

    @Test
    public void testCreateAndFindUserById() {

        user = userStorage.create(user);

        Assertions.assertEquals("Name", user.getLogin(), "Наименование не сохранилось");
        Assertions.assertNotEquals(0, user.getId(), "Ид не присвоилось");

        User fromStorage = userStorage.getUserById(user.getId());
        Assertions.assertEquals(user.getName(), fromStorage.getName(), "Это разные пользователи");
        Assertions.assertEquals(1, userStorage.findAll().size(), "Количество пользователей" +
                " не совпадает");
    }

    @Test
    public void testUserUpdate() {
        user = userStorage.create(user);

        Assertions.assertEquals(user.getLogin(), "Name", "Это разные пользователи");
        user.setLogin("First name");
        user = userStorage.update(user);
        Assertions.assertEquals(user.getLogin(), "First name", "Это разные пользователи");
    }

    @Test
    public void testRemoveUser() {
        user = userStorage.create(user);
        userStorage.remove(user);

        Assertions.assertEquals(userStorage.findAll().size(), 0, "Пользователь не удаляется");
    }


}