package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private final User user = new User(1L, "name", "user@user.ru");

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUserTest() {
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        User result = userService.createUser(user);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user, result);
    }

    @Test
    void updateUserTest() {
        User updateUser = new User(1L, "nameUpdate", "userUpdate@user.ru");
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(updateUser)).thenReturn(updateUser);
        User result = userService.updateUser(1L, updateUser);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(updateUser, result);
    }

    @Test
    void updateUserWithoutNameAndEmailTest() {
        User userBad = new User(1L, null, null);
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        User result = userService.updateUser(1L, userBad);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user, result);
    }

    @Test
    void getUserByIdTest() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
        User result = userService.getUserById(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user, result);
    }

    @Test
    void getUserByIncorrectIdTest() {
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void getAllUsersTest() {
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> result = userService.getAllUsers();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(user, result.get(0));
    }

    @Test
    void deleteUserTest() {
        userService.deleteUser(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }
}