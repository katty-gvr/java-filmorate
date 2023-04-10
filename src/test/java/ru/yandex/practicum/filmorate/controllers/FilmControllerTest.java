package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.daoImpl.LikesDaoImpl;
import ru.yandex.practicum.filmorate.storage.film.storageImpl.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {
    @Test
    void shouldCreateFilm() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        filmController.createFilm(film);
        final Collection<Film> films = filmController.findAll();
        assertNotNull(films, "Список фильмов пуст.");
        assertEquals(1, films.size());
    }

    @Test
    void shouldNotCreateFilmWithEmptyName() {
        Film film = new Film(null, "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        final Collection<Film> films = filmController.findAll();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(0, films.size());
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() {
        Film film = new Film("Home alone", "The McCallister family is preparing to spend Christmas in " +
                "Paris, gathering at Kate and Peter's home in a Chicago suburb on the night before their departure. " +
                "Kate and Peter's youngest son, Kevin, is the subject of ridicule by his older siblings and cousins. ",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        final Collection<Film> films = filmController.findAll();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(0, films.size());
    }

    @Test
    void shouldNotCreateFilmWithBadReleaseDate() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1890, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        final Collection<Film> films = filmController.findAll();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(0, films.size());
    }

    @Test
    void shouldNotCreateFilmWithBadDuration() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), -90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        final Collection<Film> films = filmController.findAll();
        assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        assertEquals(0, films.size());
    }

    //updating films

    @Test
    void shouldUpdateFilm() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        filmController.createFilm(film);
        Film updatedFilm = new Film(1, "UPD Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        final Collection<Film> films = filmController.findAll();
        assertNotNull(films, "Список фильмов пуст.");
        assertEquals(1, films.size());
        assertNotEquals(film, updatedFilm);
    }

    @Test
    void shouldNotUpdateFilmWithEmptyName() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        filmController.createFilm(film);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(new Film(1, null,
                "Christmas film", LocalDate.of(1990, 11, 10), 90)));
    }

    @Test
    void shouldNotUpdateFilmWithLongDescription() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        filmController.createFilm(film);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(new Film(1, "Home alone",
                "The McCallister family is preparing to spend Christmas in Paris, gathering at Kate and " +
                        "Peter's home in a Chicago suburb on the night before their departure. Kate and Peter's youngest son, " +
                        "Kevin, is the subject of ridicule by his older siblings and cousins. ",
                LocalDate.of(1990, 11, 10), 90)));
    }

    @Test
    void shouldNotUpdateFilmWithBadReleaseDate() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        filmController.createFilm(film);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(new Film(1, "Home alone",
                "Christmas film", LocalDate.of(1890, 11, 10), 90)));
    }

    @Test
    void shouldNotUpdateFilmWithBadDuration() {
        Film film = new Film("Home alone", "Christmas film",
                LocalDate.of(1990, 11, 10), 90);
        FilmController filmController = new FilmController(new FilmService(new InMemoryFilmStorage(),
                new LikesDaoImpl(new JdbcTemplate())));
        filmController.createFilm(film);

        assertThrows(ValidationException.class, () -> filmController.updateFilm(new Film(1, "Home alone",
                "Christmas film", LocalDate.of(1990, 11, 10), -90)));
    }
}

