package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        filmService.createFilm(film);
        log.debug("Сохранен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        filmService.updateFilm(film);
        log.debug("Обновлен фильм: {}", film);
        return film;
    }

    @GetMapping("/{id}") // получение фильма по айди
    public Film findFilm(@PathVariable("id") Integer id) {
        return filmService.findFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}") //  пользователь ставит лайк фильму.
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.addLike(filmId, userId);
        log.debug("Пользователь c id {} поставил лайк фильму с id {}", userId, filmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(filmId, userId);
        log.debug("Пользователь c id {} удалил лайка с фильма с id {}", userId, filmId);

    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        log.debug("Получен запрос на вывод {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }
}
