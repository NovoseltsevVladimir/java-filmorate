package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    private static final String FIND_BY_ID_QUERY  = "SELECT * FROM user WHERE id = ?";
    private static final String FIND_ALL  = "SELECT * FROM user";
    private static final String INSERT_QUERY = "INSERT INTO user(name, login, email,birthday) " +
            "VALUES (?, ?, ?, ?) returning id";
    private static final String UPDATE_QUERY = "UPDATE user SET name = ?, login = ?, email = ?, "+
            "birthday = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM user WHERE id = ?";

    @Autowired
    public UserRepository(JdbcTemplate jdbc, UserRowMapper mapper) {
        super(jdbc, mapper, User.class);
        this.repositoryLog = LoggerFactory.getLogger(this.getClass());
    }

    public Optional<User> findById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<User> findAll() {
        return findMany(FIND_ALL);
    }

    public User save(User user) {
        int userId = insert(
                INSERT_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Timestamp.from(Instant.from(user.getBirthday()))
        );

        user.setId(userId);

        return user;
    }

    public User update(User user) {
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Timestamp.from(Instant.from(user.getBirthday()))
        );

        return user;
    }

    public boolean delete(int id) {

        return delete(DELETE_QUERY, id);
    }
}
