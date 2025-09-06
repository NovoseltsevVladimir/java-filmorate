package ru.yandex.practicum.filmorate.dal;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM filmorate_user WHERE id = ?";
    private static final String FIND_ALL = "SELECT * FROM filmorate_user";
    private static final String INSERT_QUERY = "INSERT INTO filmorate_user (name, login, email, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE filmorate_user SET name = ?, login = ?, email = ?, " +
            "birthday = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM filmorate_user WHERE id = ?";

    private static final String ADD_FRIENDS_QUERY = "INSERT INTO friendship (user_id,friend_id,approved)" +
            "VALUES (?, ?, ?)";

    private static final String DELETE_FRIENDS_QUERY = "DELETE FROM friendship WHERE user_id = ?";

    private static final String FIND_FRIENDS_BY_ID_QUERY = "SELECT friend_id FROM friendship WHERE approved" +
            " AND user_id = ?";

    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

    @Autowired
    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
        this.repositoryLog = LoggerFactory.getLogger(this.getClass());
    }

    public Optional<User> findById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    //public List<User> findAll() {
    public List<User> findAll() {
        return findMany(FIND_ALL);
    }

    public User save(User user) {
        int userId = insert(
                INSERT_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay())
        );

        for (Integer friendId : user.getFriends()) {
            insert(
                    ADD_FRIENDS_QUERY,
                    userId,
                    friendId,
                    true
            );
        }

        user.setId(userId);

        return user;
    }

    public User update(User user) {
        int userId = user.getId();
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay()),
                user.getId()
        );

        if (user.getFriends() != null) {
            delete(DELETE_FRIENDS_QUERY, userId);

            for (Integer friendId : user.getFriends()) {
                insert(
                        ADD_FRIENDS_QUERY,
                        userId,
                        friendId,
                        true
                );
            }
        }

        return user;
    }

    public User updateFriends(User user) {

        int userId = user.getId();

        delete(DELETE_FRIENDS_QUERY, userId);

        for (Integer friendId : user.getFriends()) {
            insert(
                    ADD_FRIENDS_QUERY,
                    userId,
                    friendId,
                    true
            );
        }

        return user;
    }

    public boolean delete(int id) {
        return delete(DELETE_QUERY, id);
    }

    public boolean deleteFriend(int userId, int friendId) {
        int rowsDeleted = jdbc.update(DELETE_FRIEND_QUERY, userId, friendId);
        return rowsDeleted > 0;
    }

    public List<Integer> getFriends(User user) {
        int userId = user.getId();

        RowMapper<Integer> mapper = new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("friend_id");
            }
        };

        return jdbc.query(FIND_FRIENDS_BY_ID_QUERY, mapper, userId);

    }


}
