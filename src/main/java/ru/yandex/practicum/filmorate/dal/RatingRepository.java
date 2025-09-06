package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<Rating> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM rating";
    private static final String INSERT_QUERY = "INSERT INTO rating(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE rating SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM rating WHERE id = ?";

    public RatingRepository(JdbcTemplate jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper, Rating.class);
        this.repositoryLog = LoggerFactory.getLogger(this.getClass());
    }

    public Optional<Rating> findById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Rating> findAll() {
        return findMany(FIND_ALL);
    }

    public Rating save(Rating rating) {
        int id = insert(
                INSERT_QUERY,
                rating.getName()
        );

        rating.setId(id);

        return rating;
    }

    public Rating update(Rating rating) {
        update(
                UPDATE_QUERY,
                rating.getId()
        );

        return rating;
    }

    public boolean delete(int id) {

        return delete(DELETE_QUERY, id);
    }

}
