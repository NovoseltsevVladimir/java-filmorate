package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM genre";
    private static final String INSERT_QUERY = "INSERT INTO genre(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE genre SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM genre WHERE id = ?";

    @Autowired
    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
        this.repositoryLog = LoggerFactory.getLogger(this.getClass());
    }

    public Optional<Genre> findById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL);
    }

    public Genre save(Genre genre) {
        int id = insert(
                INSERT_QUERY,
                genre.getName()
        );

        genre.setId(id);

        return genre;
    }

    public Genre update(Genre genre) {
        update(
                UPDATE_QUERY,
                genre.getId()
        );

        return genre;
    }

    public boolean delete(int id) {

        return delete(DELETE_QUERY, id);
    }

}
