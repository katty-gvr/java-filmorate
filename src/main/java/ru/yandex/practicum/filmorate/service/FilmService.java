package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.LikesDao;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final LikesDao likesDao;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, LikesDao likesDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.likesDao = likesDao;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.findAll();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film findFilmById(Integer filmId) {
        return filmStorage.findFilmById(filmId);
    }

    public void addLike(Integer filmId, Integer userId) {
        likesDao.addLikeToFilm(filmId, userId);

    }

    public void deleteLike(Integer filmId, Integer userId) {
        likesDao.deleteLikeFromFilm(filmId, userId);
    }

    public Collection<Film> getPopularFilms(Integer count) {
        return filmStorage.findPopularFilms(count);
    }
}

