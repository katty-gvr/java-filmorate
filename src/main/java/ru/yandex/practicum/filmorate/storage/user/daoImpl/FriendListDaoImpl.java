package ru.yandex.practicum.filmorate.storage.user.daoImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Component
public class FriendListDaoImpl implements FriendListDao {

    private final JdbcTemplate jdbcTemplate;

    public FriendListDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        if (Objects.equals(userId, friendId)) {
            throw new ValidationException("Id пользователей не должны совпадать!");
        } else if (!checkUserId(userId) || !checkUserId(friendId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        String sqlQuery = "INSERT INTO friend_list(user_id, friend_id, confirmed)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, true);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        if (!checkUserId(userId) || !checkUserId(friendId)) {
            throw new ValidationException("Введен некорректный id");
        }
        String sqlQuery = "DELETE FROM friend_list WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public Collection<User> getAll(Integer id) {
        String sql = "SELECT u.user_id AS id,u.user_login,u.user_name,u.user_email,u.user_birthday " +
                "FROM friend_list AS f " +
                "LEFT JOIN users AS u ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? AND f.confirmed = TRUE";

        return jdbcTemplate.query(sql, this::rowMapper, id);
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        String sql = "SELECT  u.* " +
                "FROM friend_list AS fs " +
                "JOIN users AS u ON fs.friend_id = u.user_id " +
                "WHERE fs.user_id = ? AND fs.friend_id IN (" +
                "SELECT friend_id FROM friend_list WHERE user_id = ?)";

        return jdbcTemplate.query(sql, this::rowMapper, userId, otherId);
    }

    private User rowMapper(ResultSet resultSet, int i) throws SQLException {
        return new User(resultSet.getInt("user_id"),
                resultSet.getString("user_email"),
                resultSet.getString("user_login"),
                resultSet.getString("user_name"),
                resultSet.getDate("user_birthday").toLocalDate()
        );
    }

    private boolean checkUserId(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }
}
