package ru.yandex.practicum.filmorate.storage.film.storageImpl;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer generatorId = 0;
    final LocalDate LATEST_RELEASE_DATE = LocalDate.of(1895, 12,28);

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
        film.setId(++generatorId);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if(!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Такого фильма не существует!");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findFilmById(Integer filmId) {
        if(films.get(filmId) != null) {
            return films.get(filmId);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
    }

    @Override
    public Collection<Film> findPopularFilms(Integer count) {
        Collection<Film> films = findAll();
        return films.stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());

    }

    private void validateFilm(Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        if(film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не должно превышать 200 символов!");
        }
        if(film.getReleaseDate().isBefore(LATEST_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        if(film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }
}
