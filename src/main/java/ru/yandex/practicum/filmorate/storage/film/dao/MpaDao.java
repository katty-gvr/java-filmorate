package ru.yandex.practicum.filmorate.storage.film.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {

    Collection<Mpa> getAllMpa();

    Mpa getMpaById(int mpaId);
}
