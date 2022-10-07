package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictingException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createUser(User user) {
        if (user.getEmail() == null || user.getName() == null || !user.getEmail().contains("@") || user.getEmail().isBlank()) {
            log.error("Не заполнены поля для создания пользователя");
            throw new IncorrectRequestException("Не заполнены обязательные поля: имя, e-mail.");
        }
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.error("Адрес почты {} уже используется", user.getEmail());
            throw new ConflictingException("Адрес электронной почты уже используется другим пользователем.");
        }
    }

    @Override
    @Transactional
    public User updateUser(Long userId, User updateUser) {
        User user = getUserById(userId);
        if (updateUser.getEmail() != null && user.getEmail().contains("@")) {
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        try {
            return userRepository.save(user);
        } catch (ConflictingException e) {
            log.error("Пользователь с таким email {} уже существует.", updateUser.getEmail());
            throw new ConflictingException(String.format("Пользователь с таким email %s уже существует.",
                    updateUser.getEmail()));
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id " + userId));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void checkIfUserExists(Long userId) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("Не найден пользователь с id " + userId);
    }
}