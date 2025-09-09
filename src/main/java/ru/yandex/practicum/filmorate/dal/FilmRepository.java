package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_MANY_BY_ID_QUERY = "SELECT f.id, f.name, f.description, f.releaseDate, " +
            "f.duration, f.mpa AS mpaId, r.name AS mpaName FROM film as f " +
            "LEFT JOIN rating as r ON f.mpa = r.id WHERE f.id IN (%s)";
    private static final String FIND_ALL = "SELECT f.id, f.name, f.description, f.releaseDate," +
            "f.duration, f.mpa AS mpaId, r.name AS mpaName FROM film AS f " +
            "LEFT JOIN rating AS r ON f.mpa = r.id";
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
    private static final String FIND_FILM_GENRES = "SELECT f.film_id, f.genre_id, g.name FROM film_genre AS f" +
            " LEFT JOIN genre AS g ON f.genre_id = g.id WHERE f.film_id IN (%s)";
    private static final String FIND_FILM_LIKES = "SELECT * FROM film_like WHERE film_id IN (%s)";

    @Autowired
    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
        this.repositoryLog = LoggerFactory.getLogger(this.getClass());
    }

    public Film findById(int filmId) {

        List<Integer> filmsId = new ArrayList<>();
        filmsId.add(filmId);

        List<Film> films = getFilms(filmsId);

        if (films.size() == 0) {
            return null;
        } else {
            return films.get(0);
        }
    }

    public List<Film> findAll() {
        return getFilms(new ArrayList<>());
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

    public List<Film> getFilms(List<Integer> filmsId) {
        List<Film> filmsList;

        if (filmsId.size() == 0) {
            filmsList = findMany(FIND_ALL);
        } else {
            String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));
            filmsList = findMany(String.format(FIND_MANY_BY_ID_QUERY, inSql), filmsId.toArray());
        }

        getAndSetFilmsLikes(filmsList);
        getAndSetFilmsGenres(filmsList);

        return filmsList;
    }

    public List<Film> getAndSetFilmsLikes(List<Film> filmsList) {

        if (filmsList.size() == 0) {
            return filmsList;
        }

        List<Integer> filmsId = new ArrayList<>();
        Map<Integer, Film> films = new HashMap<>();

        for (Film film : filmsList) {
            Integer filmId = film.getId();

            filmsId.add(film.getId());
            films.put(filmId, film);
        }

        //Лайки
        RowMapper<HashMap<Integer, Integer>> likesMapper = new RowMapper<HashMap<Integer, Integer>>() {
            @Override
            public HashMap<Integer, Integer> mapRow(ResultSet rs, int rowNum) throws SQLException {
                HashMap<Integer, Integer> filmAndLike = new HashMap<>();
                filmAndLike.put(rs.getInt("film_id"), rs.getInt("user_id"));
                return filmAndLike;
            }
        };

        String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));
        List<HashMap<Integer, Integer>> likesList = jdbc.query(String.format(FIND_FILM_LIKES, inSql),
                likesMapper,
                filmsId.toArray());

        Map<Integer, Set> filmsLikes = new HashMap<>();

        for (HashMap<Integer, Integer> filmAndLike : likesList) {
            for (Integer filmId : filmAndLike.keySet()) {
                Set<Integer> likes;
                if (!filmsLikes.containsKey(filmId)) {
                    likes = new HashSet<>();
                } else {
                    likes = filmsLikes.get(filmId);
                }

                likes.add(filmAndLike.get(filmId));
                filmsLikes.put(filmId, likes);
            }
        }

        for (Integer filmId : filmsLikes.keySet()) {
            Film film = films.get(filmId);
            film.setUsersIdWithLikes(filmsLikes.get(filmId));
        }

        return films.values().stream().toList();

    }

    public List<Film> getAndSetFilmsGenres(List<Film> filmsList) {

        if (filmsList.size() == 0) {
            return filmsList;
        }

        List<Integer> filmsId = new ArrayList<>();
        Map<Integer, Film> films = new HashMap<>();

        for (Film film : filmsList) {
            Integer filmId = film.getId();

            filmsId.add(film.getId());
            films.put(filmId, film);
        }
        //Жанры
        RowMapper<HashMap<Integer, Genre>> genresMapper = new RowMapper<HashMap<Integer, Genre>>() {
            @Override
            public HashMap<Integer, Genre> mapRow(ResultSet rs, int rowNum) throws SQLException {

                HashMap<Integer, Genre> filmAndGenre = new HashMap<>();

                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("name"));

                filmAndGenre.put(rs.getInt("film_id"), genre);
                return filmAndGenre;
            }
        };

        String inSql = String.join(",", Collections.nCopies(filmsId.size(), "?"));
        List<HashMap<Integer, Genre>> genresList = jdbc.query(String.format(FIND_FILM_GENRES, inSql),
                genresMapper,
                filmsId.toArray());

        Map<Integer, Set<Genre>> filmsGenres = new HashMap<>();

        for (HashMap<Integer, Genre> filmAndGenre : genresList) {
            for (Integer filmId : filmAndGenre.keySet()) {
                Set<Genre> genres;
                if (!filmsGenres.containsKey(filmId)) {
                    genres = new HashSet<>();
                } else {
                    genres = filmsGenres.get(filmId);
                }

                genres.add(filmAndGenre.get(filmId));
                filmsGenres.put(filmId, genres);
            }
        }

        for (Integer filmId : filmsGenres.keySet()) {
            Film film = films.get(filmId);
            film.setGenres(filmsGenres.get(filmId));
        }

        return films.values().stream().toList();

    }
}
