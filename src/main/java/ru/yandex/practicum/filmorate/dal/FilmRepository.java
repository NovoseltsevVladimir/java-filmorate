package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM film";
    private static final String INSERT_QUERY = "INSERT INTO film(name, description, releaseDate,duration,mpa) " +
            "VALUES (?, ?, ?, ?,?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, releaseDate = ?, " +
            "duration = ?,mpa = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE id = ?";
    private static final String INSERT_LIKES = "INSERT INTO film_like(film_id, user_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_LIKES = "DELETE FROM film_like WHERE film_id = ?";

    private static final String INSERT_GENRES = "INSERT INTO film_genre(film_id, genre_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_GENRES = "DELETE FROM film_genre WHERE film_id = ?";

    private static final String FIND_FILM_GENRES = "SELECT * FROM film_genre WHERE film_id = ? Order BY genre_id ASC";

    private static final String FIND_FILM_MPA = "SELECT * FROM rating " +
            " INNER JOIN film ON rating.id = film.mpa WHERE film.id = ?";

    private static final String FIND_FILM_LIKES = "SELECT * FROM film_like WHERE film_id = ?";

    @Autowired
    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
        this.repositoryLog = LoggerFactory.getLogger(this.getClass());
    }

    public Optional<Film> findById(int filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL);
    }

    public Film save(Film film) {
        int filmId = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                (film.getMpa() == null) ? null : film.getMpa().getId()
        );

        for (int userId : film.getUsersIdWithLikes()) {
            insert(
                    INSERT_LIKES,
                    filmId,
                    userId
            );
        }

        for (Genre genre : film.getGenres()) {
            insert(
                    INSERT_GENRES,
                    filmId,
                    genre.getId()
            );
        }

        film.setId(filmId);

        return film;
    }

    public Film update(Film film) {

        int filmId = film.getId();

        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                (film.getMpa() == null) ? null : film.getMpa().getId(),
                filmId
        );

        delete(DELETE_LIKES, filmId);
        delete(DELETE_GENRES, filmId);

        for (int userId : film.getUsersIdWithLikes()) {
            insert(
                    INSERT_LIKES,
                    filmId,
                    userId
            );
        }

        for (Genre genre : film.getGenres()) {
            insert(
                    INSERT_GENRES,
                    filmId,
                    genre.getId()
            );
        }

        return film;
    }

    public boolean delete(int id) {

        return delete(DELETE_QUERY, id);
    }

    public List<Integer> getFilmGenreId(Film film) {

        int filmId = film.getId();

        RowMapper<Integer> mapper = new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("genre_id");
            }
        };

        return jdbc.query(FIND_FILM_GENRES, mapper, filmId);

    }

    public Rating getFilmMpa(Film film) {

        Integer filmId = film.getId();
        if (filmId == null) {
            return null;
        }

        RowMapper<Rating> mapper = new RowMapper<Rating>() {
            @Override
            public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
                Rating rating = new Rating();
                rating.setId(rs.getInt("id"));
                rating.setName(rs.getString("name"));

                return rating;
            }
        };

        Rating rating = jdbc.queryForObject(FIND_FILM_MPA, mapper, filmId);

        return rating;

    }

    public List<Integer> getFilmLikes(Film film) {

        Integer filmId = film.getId();
        if (filmId == null) {
            return null;
        }

        RowMapper<Integer> mapper = new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("user_id");
            }
        };

        return jdbc.query(FIND_FILM_LIKES, mapper, filmId);

    }
}
