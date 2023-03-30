package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.LikesDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.storageImpl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.daoImpl.FriendListDaoImpl;
import ru.yandex.practicum.filmorate.storage.user.storageImpl.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FriendListDaoImpl friendListDao;
    private final LikesDaoImpl likesStorage;
    private final MpaDaoImpl mpaDbStorage;
    private final GenreDaoImpl genreDbStorage;

    @Test
    public void testFindAllUsers() {
        Collection<User> users = userStorage.findAll();

        assertThat(users)
                .isNotEmpty();
    }

    @Test
    public void testCreateUser() {
        User user = new User("test@test.com", "test", "Test User", LocalDate.of(1990, 10, 10));
        userStorage.createUser(user);

        assertThat(user.getId()).isNotNull();
        assertThat(userStorage.findUserById(user.getId())).isEqualTo(user);
    }

    @Test
    public void testUpdateUser() {
        User user = new User("test@test.com", "test", "Test User", LocalDate.of(1990, 10, 10));
        userStorage.createUser(user);
        User updatedUser = new User(user.getId(), "updated@test.com", "updated", "Updated User",
                LocalDate.of(1990, 10, 11));
        userStorage.updateUser(updatedUser);

        assertThat(userStorage.findUserById(updatedUser.getId()))
                .isEqualTo(updatedUser);
    }

    @Test
    public void testFindUserByIdThrowsUserNotFoundException() {
        assertThatThrownBy(() -> userStorage.findUserById(999))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь с идентификатором 999 не найден");
    }

    @Test
    public void testAddFriend() {
        int userId = 1;
        int friendId = 2;
        friendListDao.addFriend(userId, friendId);
        Collection<User> friends = friendListDao.getAll(userId);

        assertThat(friends)
                .isNotNull()
                .hasSize(1);
    }

    @Test
    public void testAddFriendSameIdsThrowsValidationException() {
        int userId = 1;
        int friendId = 1;
        assertThatThrownBy(() -> friendListDao.addFriend(userId, friendId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Id пользователей не должны совпадать!");
    }

    @Test
    public void testAddFriendNonExistingIdsThrowsUserNotFoundException() {
        int userId = 1;
        int friendId = 999;
        assertThatThrownBy(() -> friendListDao.addFriend(userId, friendId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    public void testDeleteFriend() {
        int userId = 1;
        int friendId = 2;
        friendListDao.addFriend(userId, friendId);
        friendListDao.deleteFriend(userId, friendId);
        friendListDao.deleteFriend(userId, 3);
        Collection<User> friends = friendListDao.getAll(userId);

        assertThat(friends)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    public void testDeleteFriendNonExistingIdsThrowsValidationException() {
        int userId = 1;
        int friendId = 999;
        assertThatThrownBy(() -> friendListDao.deleteFriend(userId, friendId))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Введен некорректный id");
    }

    @Test
    public void testGetAllFriendsOfUser() {
        Collection<User> friends = friendListDao.getAll(1);

        assertThat(friends)
                .isNotNull();
    }

    @Test
    public void testGetCommonFriends() {
        User friend1 = new User("friend1@test.com", "friend1", "Friend1", LocalDate.of(2000, 1, 1));
        User friend2 = new User("friend2@test.com", "friend2", "Friend2", LocalDate.of(2000, 7, 1));
        User friend3 = new User("friend3@test.com", "friend 3", "Friend3",LocalDate.of(2000, 7, 1));

        userStorage.createUser(friend1);
        userStorage.createUser(friend2);
        userStorage.createUser(friend3);

        friendListDao.addFriend(friend1.getId(), friend3.getId());
        friendListDao.addFriend(friend2.getId(), friend3.getId());
        Collection<User> commonFriends = friendListDao.getCommonFriends(friend1.getId(), friend2.getId());

        assertThat(commonFriends)
                .isNotNull()
                .hasSize(1)
                .containsExactly(friend3);
    }

    @Test
    public void testFindAllFilms() {
        testCreateFilm();
        Collection<Film> films = filmStorage.findAll();
        assertThat(films).isNotEmpty();
    }

    @Test
    public void testCreateFilm() {
        Film film = new Film(null, "New Film", "New Description", LocalDate.of(2000, 1, 1), 120,
                new Mpa(1, "PG", "Рекомендуется присутствие родителей"),
                new HashSet<>(), new LinkedHashSet<>());
        Film createdFilm = filmStorage.createFilm(film);
        assertThat(createdFilm.getId()).isNotNull();
        assertThat(createdFilm.getName()).isEqualTo(film.getName());
        assertThat(createdFilm.getDescription()).isEqualTo(film.getDescription());
        assertThat(createdFilm.getReleaseDate()).isEqualTo(film.getReleaseDate());
        assertThat(createdFilm.getDuration()).isEqualTo(film.getDuration());
        assertThat(createdFilm.getMpa()).isEqualTo(film.getMpa());
        assertThat(createdFilm.getGenres()).isEmpty();
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film(1, "Updated Film", "Updated Description", LocalDate.of(2000, 1,1),
                130, new Mpa(2, "PG-13", "Детям до 13 лет просмотр не желателен"),
                new HashSet<>(), new LinkedHashSet<>());
        Film updatedFilm = filmStorage.updateFilm(film);
        assertThat(updatedFilm.getId()).isEqualTo(film.getId());
        assertThat(updatedFilm.getName()).isEqualTo(film.getName());
        assertThat(updatedFilm.getDescription()).isEqualTo(film.getDescription());
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(film.getReleaseDate());
        assertThat(updatedFilm.getDuration()).isEqualTo(film.getDuration());
        assertThat(updatedFilm.getMpa()).isEqualTo(film.getMpa());
        assertThat(updatedFilm.getGenres()).isEmpty();
    }

    @Test
    public void testFindFilmById() {
        testCreateFilm();
        Film film = filmStorage.findFilmById(1);
        assertThat(film.getId()).isEqualTo(1);
        assertThat(film.getName()).isEqualTo("Updated Film");
        assertThat(film.getDescription()).isEqualTo("Updated Description");
        assertThat(film.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1,1));
        assertThat(film.getDuration()).isEqualTo(130);
        assertThat(film.getGenres()).isEmpty();
    }

    @Test
    public void testFindPopularFilms() {
        Collection<Film> popularFilms = filmStorage.findPopularFilms(1);
        assertThat(popularFilms).hasSize(1);
    }

    @Test
    void testGetFilmLikes() {
        likesStorage.addLikeToFilm(1,1);
        likesStorage.addLikeToFilm(1,2);
        Set<Integer> actualLikes = likesStorage.getFilmLikes(1);
        assertEquals(actualLikes.size(), 2);
    }

    @Test
    void testAddLikeToFilm() {
        Integer filmId = 1;
        Integer userId = 3;
        likesStorage.addLikeToFilm(filmId, userId);

        Set<Integer> likes = likesStorage.getFilmLikes(filmId);
        assertTrue(likes.contains(userId));
    }

    @Test
    void testDeleteLikeFromFilmWithInvalidFilmId() {
        Integer invalidFilmId = 0;
        Integer userId = 1;

        assertThrows(FilmNotFoundException.class,
                () -> likesStorage.deleteLikeFromFilm(invalidFilmId, userId));
    }

    @Test
    public void testGetAllGenres() {
        Collection<Genre> genres = genreDbStorage.getAllGenres();
        assertThat(genres).isNotEmpty();
        assertEquals(6, genres.size());
    }

    @Test
    public void testGetGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);
        assertEquals("Комедия", genre.getName());
    }

    @Test
    public void testAddAndGetGenresToFilm() {
        Film film = new Film(55, "Updated Film", "Updated Description", LocalDate.of(2000, 1,1),
                130, new Mpa(2, "PG-13", "Детям до 13 лет просмотр не желателен"),
                new HashSet<>(), new LinkedHashSet<>());
        filmStorage.createFilm(film);
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(genreDbStorage.getGenreById(1));
        genres.add(genreDbStorage.getGenreById(2));
        film.setGenres(genres);
        genreDbStorage.addGenresToFilm(film);

        assertThat(film.getGenres()).isNotEmpty();
    }

    @Test
    public void testGetAllMpa() {
        Collection<Mpa> mpaRatings = mpaDbStorage.getAllMpa();
        assertThat(mpaRatings).isNotEmpty();
        assertEquals(5, mpaRatings.size());
    }

    @Test
    public void testGetMpaById() {
        Mpa mpa = mpaDbStorage.getMpaById(1);
        assertEquals("G", mpa.getName());
        assertEquals("Нет возрастных ограничений", mpa.getDescription());
    }
}


