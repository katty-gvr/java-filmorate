package ru.yandex.practicum.filmorate.storage.film.dao;

import java.util.Set;

public interface LikesDao {

    Set<Integer> getFilmLikes(Integer filmId);

    void addLikeToFilm(Integer filmId, Integer userId);

    void deleteLikeFromFilm(Integer filmId, Integer userId);
}
