package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.dao.FriendListDao;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendListDao friendListDao;

    @Autowired
    public UserService(UserStorage userStorage, FriendListDao friendListDao) {
        this.userStorage = userStorage;
        this.friendListDao = friendListDao;
    }

    public Collection<User> getAllUsers() {
        return userStorage.findAll();
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User findUserById(Integer userId) {
        return userStorage.findUserById(userId);
    }

    public void addFriend(Integer id, Integer friendId) {
        friendListDao.addFriend(id, friendId);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        friendListDao.deleteFriend(id, friendId);
    }

    public Collection<User> getFriendList(Integer id) {
        return friendListDao.getAll(id);
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        return friendListDao.getCommonFriends(id, otherId);
    }
}
