package ru.yandex.practicum.filmorate.storage.user.storageImpl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::rowMapper);
    }

    public void deleteAllData() {
        String sqlQuery = "DELETE FROM users";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (user_name, user_email, user_login, user_birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        checkUserName(user);
        if (user.getLogin().equals(user.getName())) {
            String sqlQuery1 = "UPDATE users SET user_name = ?" +
                    "WHERE user_id = ?";;
            jdbcTemplate.update(sqlQuery1, user.getName(), user.getId());

        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (checkUserId(user.getId()) == 0) {
            throw new UserNotFoundException("Пользователь с идентификатором " + user.getId() + " не найден");
        }
        String sqlQuery = "UPDATE users SET user_name = ?, user_email = ?, user_login = ?, user_birthday = ?" +
                "WHERE user_id = ?";
        checkUserName(user);
        jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User findUserById(Integer userId) {
        if (checkUserId(userId) == 0) {
            throw new UserNotFoundException("Пользователь с идентификатором " + userId + " не найден");
        }
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::rowMapper, userId);
    }

    private User rowMapper(ResultSet resultSet, int i) throws SQLException {
        return new User(resultSet.getInt("user_id"),
                resultSet.getString("user_email"),
                resultSet.getString("user_login"),
                resultSet.getString("user_name"),
                resultSet.getDate("user_birthday").toLocalDate()
        );
    }

    private int checkUserId(int id) {
        String sqlQuery = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, id);
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
