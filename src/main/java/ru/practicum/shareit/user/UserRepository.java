package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User createUser(User user);

    User updateUser(Long userId, User updateUser);

    void deleteUser(Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();
}