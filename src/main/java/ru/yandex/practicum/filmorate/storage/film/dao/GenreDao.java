package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.LinkedHashSet;


public interface GenreDao {

    Collection<Genre> getAllGenres();

    Genre getGenreById(Integer genreId);

    void addGenresToFilm(Film film);

    void deleteGenresFromFilm(Film film);

    void updateGenresOfFilm(Film film);

    LinkedHashSet<Genre> getGenresOfFilm(int filmId);
}
