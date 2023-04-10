package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.GenreDao;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreDao genreDao;

    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Collection<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return genreDao.getGenreById(id);
    }
}
