package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_BY_ID_QUERY  = "SELECT * FROM film WHERE id = ?";
    private static final String FIND_ALL  = "SELECT * FROM film";
    private static final String INSERT_QUERY = "INSERT INTO film(name, description, releaseDate,duration,rating_id) " +
            "VALUES (?, ?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, releaseDate = ?, "+
          "duration = ?,rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE id = ?";

    @Autowired
    public FilmRepository(JdbcTemplate jdbc, FilmRowMapper mapper) {
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
                Timestamp.from(Instant.from(film.getReleaseDate())),
                film.getDuration(),
                film.getRating_id()
        );

        film.setId(filmId);

        return film;
    }

    public Film update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(Instant.from(film.getReleaseDate())),
                film.getDuration(),
                film.getRating_id()
        );

        return film;
    }

    public boolean delete(int id) {

        return delete(DELETE_QUERY, id);
    }
}
