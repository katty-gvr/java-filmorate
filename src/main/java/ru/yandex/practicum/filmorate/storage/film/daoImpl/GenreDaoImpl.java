package ru.yandex.practicum.filmorate.storage.film.daoImpl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    @Override
    public Genre getGenreById(Integer genreId) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException("Жанр с идентификатором " + genreId +
                    " не найден!");
        }
        return genre;
    }

    @Override
    public void deleteGenresFromFilm(Film film) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }
    @Override
    public void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            String setNewGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(setNewGenres, film.getId(), genre.getId());
        }
    }
    @Override
    public void updateGenresOfFilm(Film film) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresToFilm(film);
    }

    @Override
    public LinkedHashSet<Genre> getGenresOfFilm(int filmId) {
        String sqlQuery = "SELECT * FROM genres " +
                "INNER JOIN films_genres AS fg ON genres.genre_id = fg.genre_id " +
                "WHERE film_id = ?";
        List<Genre> filmGenres = jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
        return new LinkedHashSet<>(filmGenres);
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("genre_name"));
    }
}
