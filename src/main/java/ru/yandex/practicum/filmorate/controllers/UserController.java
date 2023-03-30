package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        log.debug("Сохранен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        userService.updateUser(user);
        log.debug("Обновлен пользователь: {}", user);
        return user;
    }

    @GetMapping("/{id}") // получение пользователя по айди
    public User findUser(@PathVariable("id") Integer id) {
        return userService.findUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}") // добавление в друзья
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @GetMapping("/{id}/friends") // получение списка друзей пользователя
    public Collection<User> getFriendList(@PathVariable("id") Integer id) {
        return userService.getFriendList(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}") //удаление из друзей
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}") // получение списка общих друзей с другим пользователем
    public Collection<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
