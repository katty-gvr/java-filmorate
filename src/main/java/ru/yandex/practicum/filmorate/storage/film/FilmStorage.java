package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> findAll();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film findFilmById(Integer filmId);

    Collection<Film> findPopularFilms(Integer count);

}
