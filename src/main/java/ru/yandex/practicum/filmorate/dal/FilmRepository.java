package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_BY_ID_QUERY  = "SELECT * FROM film WHERE id = ?";
    private static final String FIND_ALL  = "SELECT * FROM film";
    private static final String INSERT_QUERY = "INSERT INTO film(name, description, releaseDate,duration,rating_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, releaseDate = ?, "+
          "duration = ?,rating_id = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE id = ?";

    private static final String INSERT_LIKES = "INSERT INTO film_like(film_id, user_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_LIKES = "DELETE FROM film_like WHERE film_id = ?";

    private static final String INSERT_GENRES  = "INSERT INTO film_genre(film_id, genre_id) " +
            "VALUES (?, ?)";

    private static final String DELETE_GENRES = "DELETE FROM film_genre WHERE film_id = ?";

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
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getRatingId()
        );

        for (int userId:film.getUsersIdWithLikes()) {
            insert(
                    INSERT_LIKES,
                    filmId,
                    userId
            );
        }

        for (int genreId:film.getGenresId()) {
            insert(
                    INSERT_GENRES,
                    filmId,
                    genreId
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
                film.getRatingId(),
                filmId
        );

        delete(DELETE_LIKES,filmId);
        delete(DELETE_GENRES,filmId);

        for (int userId:film.getUsersIdWithLikes()) {
            insert(
                    INSERT_LIKES,
                    film,
                    filmId
            );
        }

        for (int genreId:film.getGenresId()) {
            insert(
                    INSERT_GENRES,
                    filmId,
                    genreId
            );
        }

        return film;
    }

    public boolean delete(int id) {

        return delete(DELETE_QUERY, id);
    }
}
