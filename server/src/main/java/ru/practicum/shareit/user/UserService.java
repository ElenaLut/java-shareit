package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(Long userId, User updateUser);

    void deleteUser(Long userId);

    User getUserById(Long userId);

    List<User> getAllUsers();

    void checkIfUserExists(Long userId);
}