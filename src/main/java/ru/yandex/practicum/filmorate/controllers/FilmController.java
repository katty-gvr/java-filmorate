package ru.yandex.practicum.filmorate.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private Integer generatorId = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
       if(films.containsKey(film.getId())) {
            throw new ValidationException("Такой фильм уже существует!");
        }
        validateFilm(film);
        film.setId(++generatorId);
        films.put(film.getId(), film);
        log.debug("Сохранен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if(!films.containsKey(film.getId())) {
            throw new ValidationException("Такого фильма не существует!");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }

    private void validateFilm(Film film) {
        if(film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым!");
        }
        if(film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не должно превышать 200 символов!");
        }
        if(film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        if(film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }
}
