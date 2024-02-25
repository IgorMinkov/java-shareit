package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTests {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private User userFromDto;
    private User user;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        userFromDto = User.builder()
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        user = User.builder()
                .id(userId)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> testUserList = userService.getAll();

        assertEquals(testUserList, List.of(user));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void addUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User testUser = userService.create(userFromDto);

        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getName(), user.getName());
        assertEquals(testUser.getEmail(), user.getEmail());

        verify(userRepository, times(1)).save(userFromDto);
    }

    @Test
    void updateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userFromDto.setName("Boris");
        userFromDto.setEmail("Boris@yandex.ru");

        User testUser = userService.update(userFromDto, userId);

        assertEquals(testUser.getName(), userFromDto.getName());
        assertEquals(testUser.getEmail(), userFromDto.getEmail());
        assertEquals(testUser.getId(), user.getId());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(testUser);
        verify(userRepository, times(1)).findByEmail(userFromDto.getEmail());


    }

    @Test
    void updateUserNotUniqueEmail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(anyString())).thenReturn(List.of(user));

        User userWithWrongEmail = User.builder()
                .id(2L)
                .name("Boris")
                .email("alexFirst@yandex.ru")
                .build();

        assertThrows(EmailAlreadyExistException.class, () -> userService.update(userWithWrongEmail, 2L));
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).findByEmail(userWithWrongEmail.getEmail());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    void deleteUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.delete(userId);

        verify(userRepository, times(1)).deleteById(userId);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void getUserById() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User testUser = userService.getById(userId);

        assertEquals(testUser.getId(), user.getId());
        assertEquals(testUser.getName(), user.getName());
        assertEquals(testUser.getEmail(), user.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void checkUserFail() {
        when(userRepository.existsById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> userService.checkUser(-1L));

        verify(userRepository, times(1)).existsById(-1L);
    }

}
