package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.daoImpl.FriendListDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.storageImpl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    @Test
    void shouldCreateUser() {
        User user = new User("ivan@mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);
        final Collection<User> users = userController.findAll();
        assertNotNull(users, "Список пользователей пуст.");
        assertEquals(1, users.size());
    }

    @Test
    void shouldNotCreateUserWithEmptyEmail() {
        User user = new User(null, "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        final Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldNotCreateUserWithBadEmail() {
        User user = new User("ivan--mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        final Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldNotCreateUserWithEmptyLogin() {
        User user = new User("ivan@mail.ru", " ", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        final Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldNotCreateUserWithBadLogin() {
        User user = new User("ivan@mail.ru", "Ivan 2 3 4 3", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        final Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldCreateUserWithEmptyName() {
        User user = new User("ivan@mail.ru", "Ivan2343", null,
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);
        final Collection<User> users = userController.findAll();
        assertNotNull(users, "Список пользователей пуст.");
        assertEquals(1, users.size());
        assertEquals("Ivan2343", user.getName());
    }

    @Test
    void shouldNotCreateUserWithBadBirthday() {
        User user = new User("ivan@mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(2025, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        final Collection<User> users = userController.findAll();
        assertThrows(ValidationException.class, () -> userController.createUser(user));
        assertEquals(0, users.size());
    }

    @Test
    void shouldUpdateUser() {
        User user = new User("ivan@mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);
        User updatedUser = new User(1, "ivan@mail.ru", "UpdatedIvan2343", "UPDIvan",
                LocalDate.of(1995, 5, 5));
        userController.updateUser(updatedUser);
        assertNotEquals(user, updatedUser);
    }

    @Test
    void shouldNotUpdateUserWithEmptyEmail() {
        User user = new User("ivan@mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(new User(1, null,
                "Ivan2343", "Ivan", LocalDate.of(1995, 5, 5))));
    }

    @Test
    void shouldNotUpdateUserWithBadEmail() {
        User user = new User("ivan@mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(new User(1, "ivan--mail.ru",
                "Ivan2343", "Ivan", LocalDate.of(1995, 5, 5))));
    }

    @Test
    void shouldNotUpdateUserWithEmptyLogin() {
        User user = new User("ivan@mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(new User(1, "ivan@mail.ru",
                " ", "Ivan", LocalDate.of(1995, 5, 5))));

    }

    @Test
    void shouldNotUpdateUserWithBadLogin() {
        User user = new User("ivan@mail.ru", "Ivan2343", "Ivan",
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(new User(1, "ivan@mail.ru",
                "Ivan2 3 4 3", "Ivan", LocalDate.of(1995, 5, 5))));
    }

    @Test
    void shouldUpdateUserWithEmptyName() {
        User user = new User("ivan@mail.ru", "Ivan2343", null,
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);
        User updatedUser = new User(1, "ivan@mail.ru", "UPDIvan2343", null,
                LocalDate.of(1995, 5, 5));
        userController.updateUser(updatedUser);
        final Collection<User> users = userController.findAll();
        assertNotNull(users, "Список пользователей пуст.");
        assertEquals(1, users.size());
        assertEquals("Ivan2343", user.getName());
        assertNotEquals(user, updatedUser);
    }

    @Test
    void shouldNotUpdateUserWithBadBirthday() {
        User user = new User("ivan@mail.ru", "Ivan2343", null,
                LocalDate.of(1995, 5, 5));
        UserController userController = new UserController(new UserService(new InMemoryUserStorage(),
                new FriendListDaoImpl(new JdbcTemplate())));
        userController.createUser(user);

        assertThrows(ValidationException.class, () -> userController.updateUser(new User(1, "ivan@mail.ru",
                "Ivan2343", "Ivan", LocalDate.of(2025, 5, 5))));
    }
}

