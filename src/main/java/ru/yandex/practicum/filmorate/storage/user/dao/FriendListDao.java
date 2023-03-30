package ru.yandex.practicum.filmorate.storage.user.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendListDao {

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer id, Integer friendId);

    Collection<User> getAll(Integer id);

    public Collection<User> getCommonFriends(Integer id, Integer otherId);

}
