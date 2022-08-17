package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private GeneratorUserId generatorUserId = new GeneratorUserId();

    @Override
    public User createUser(User user) {
        checkUniqueEmail(user.getEmail());
        checkEmail(user);
        user.setId(generatorUserId.generate());
        users.put(user.getId(), user);
        log.debug("Создан пользователь с id {}.", user.getId());
        return user;
    }

    @Override
    public User updateUser(Long userId, User updateUser) {
        checkUserId(userId);
        User user = getUserById(userId);
        if (updateUser.getEmail() != null & user.getEmail().contains("@")) {
            checkUniqueEmail(updateUser.getEmail());
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }

    @Override
    public void deleteUser(Long userId) {
        checkUserId(userId);
        users.remove(userId);
        log.info("Пользователь с id {} удален", userId);
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            log.warn("Пользователь с id {} не существует", userId);
            throw new NotFoundException("Пользователь не существует");
        }
        return users.get(userId);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Возвращен список всех пользователей");
        return new ArrayList<>(users.values());
    }

    private void checkUniqueEmail(String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                log.warn("Электронный адрес {} уже существует", user.getEmail());
                throw new ConflictingException("Пользователь с указанной электронной почтой уже создан.");
            }
        }
    }

    private void checkUserId(long id) {
        if (getUserById(id) == null) {
            log.warn("Пользователя с id {} не существует", id);
            throw new NotFoundException("Пользователь не существует");
        }
    }

    private void checkEmail(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @, текущая: {}", user.getEmail());
            throw new IncorrectRequestException("электронная почта не может быть пустой и должна содержать символ @");
        }
    }
}