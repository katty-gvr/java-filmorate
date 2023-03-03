package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
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
        User user = findUserById(id);
        User friend = findUserById(friendId);

        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(id);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        User user = findUserById(id);
        User friend = findUserById(friendId);

        user.getFriendIds().remove(friend.getId());
        friend.getFriendIds().remove(user.getId());
    }

    public Collection<User> getFriendList(Integer id) {
        List<User> friends = new ArrayList<>();
        Set<Integer> friendIds = findUserById(id).getFriendIds();

        for(Integer friendId : friendIds) {
            User friend = findUserById(friendId);
            friends.add(friend);
        }
        return friends;
    }

    public Collection<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> commonFriends = new ArrayList<>();
        Set<Integer> userFriends = findUserById(id).getFriendIds();
        Set<Integer> otherUserFriends = findUserById(otherId).getFriendIds();

        Set<Integer> commonIds = new HashSet<>(userFriends);
        commonIds.retainAll(otherUserFriends);
        for(Integer comId : commonIds) {
            commonFriends.add(findUserById(comId));
        }
        return commonFriends;
    }
}
