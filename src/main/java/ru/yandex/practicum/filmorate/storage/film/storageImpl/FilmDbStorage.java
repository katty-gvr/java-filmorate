package ru.yandex.practicum.filmorate.storage.film.storageImpl;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;

import java.sql.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final LikesDao likesDao;

    final LocalDate latestReleaseDate = LocalDate.of(1895, 12,28);

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDao genreDao, LikesDao likesDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.likesDao = likesDao;
    }

    @Override
    public Collection<Film> findAll() {
        String sqlQuery = "SELECT f.*, " +
                "m.rating as mpa_name, " +
                "m.description as mpa_description, " +
                "m.rating_id as mpa_id " +
                "FROM films as f " +
                "JOIN mpa_ratings as m ON f.mpa_id = m.rating_id ";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id)"
                + "values (?, ?, ?, ?, ?)";
        if (film.getReleaseDate().isBefore(latestReleaseDate)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());

        if (film.getGenres() != null) {
            addGenresToFilm(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!checkFilmId(film.getId())) {
            throw new FilmNotFoundException("Фильм с идентификатором " + film.getId() + " не найден!");
        }
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            deleteGenresFromFilm(film);
        } else {
            film.setGenres(new LinkedHashSet<>(film.getGenres()));
            updateGenresOfFilm(film);
        }
        return film;
    }

   @Override
    public Film findFilmById(Integer id) {
       if (!checkFilmId(id)) {
           throw new FilmNotFoundException("Фильм с идентификатором " + id + " не найден!");
       }
       String sqlQuery = "SELECT f.*, " +
               "m.rating as mpa_name, " +
               "m.description as mpa_description, " +
               "m.rating_id as mpa_id, " +
               "FROM films as f " +
               "JOIN mpa_ratings as m ON f.mpa_id = m.rating_id " +
               "WHERE film_id = ?";
       Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
       updateGenresOfFilm(film);
       return film;
    }

    @Override
    public Collection<Film> findPopularFilms(Integer count) {
        String sqlQuery = "SELECT f.*, " +
                "m.rating as mpa_name, " +
                "m.rating_id as mpa_id, " +
                "m.description as mpa_description, " +
                "FROM films as f " +
                "JOIN mpa_ratings as m ON f.mpa_id = m.rating_id " +
                "LEFT JOIN films_likes as l ON l.film_id = f.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, count);
    }

    public void deleteGenresFromFilm(Film film) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    public void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            String setNewGenres = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(setNewGenres, film.getId(), genre.getId());
        }
    }

    public void updateGenresOfFilm(Film film) {
        String sqlQuery = "DELETE FROM films_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
        addGenresToFilm(film);
    }

    public Collection<Genre> getGenresOfFilm(int filmId) {
        String sqlQuery = "SELECT * FROM genres " +
                "INNER JOIN films_genres AS fg ON genres.genre_id = fg.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
    }

   private Film makeFilm(ResultSet resultSet, int i) throws SQLException {
        return new Film(
                resultSet.getInt("film_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getInt("duration"),
                new Mpa(resultSet.getInt("mpa_id"),
                        resultSet.getString("mpa_name"),
                        resultSet.getString("mpa_description")),
                likesDao.getFilmLikes(resultSet.getInt("film_id")),
                getGenresOfFilm(resultSet.getInt("film_id"))
        );
   }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("genre_name"));
    }

    private boolean checkFilmId(int id) {
        String sql = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, id);
    }
}
