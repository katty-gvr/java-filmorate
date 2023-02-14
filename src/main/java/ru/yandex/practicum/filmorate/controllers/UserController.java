package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer generatorId = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(++generatorId);
        users.put(user.getId(), user);
        log.debug("Сохранен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователя с таким id не существует!");
        }
        validateUser(user);
        users.put(user.getId(), user);
        log.debug("Обнлвен пользователь: {}", user);
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Адрес электронной почты не может быть пустым!");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Адрес электронной почты должен содержать символ @");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы!");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем!");
        }
    }
}
