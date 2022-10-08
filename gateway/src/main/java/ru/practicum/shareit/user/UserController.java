package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserDto userDto) {
        log.info("Запрос на создание пользователя {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long userId,
                                         @RequestBody UserDto userDto) {
        log.info("Запрос на обновление пользователя {}", userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") Long userId) {
        log.info("Запрос на удаление пользователя {}", userId);
        return userClient.deleteUser(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable("id") Long userId) {
        log.info("Запрос на получение пользователя {}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userClient.getAllUsers();
    }
}