package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTests {

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private UserDto testUserDto;
    private Long testUserId;

    @BeforeEach
    void setUp() {

        testUserId = 1L;

        testUserDto = UserDto.builder()
                .id(testUserId)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();
    }

    @Test
    void addUserShouldReturn200AndUserDto() throws Exception {
        when(userService.create(any(User.class)))
                .thenReturn(UserMapper.toUser(testUserDto));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(testUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testUserDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(testUserDto.getEmail()), String.class));

        verify(userService, times(1)).create(UserMapper.toUser(testUserDto));
    }

    @Test
    void updateUserShouldReturn200AndUserDto() throws Exception {
        when(userService.update(any(User.class), anyLong()))
                .thenReturn(UserMapper.toUser(testUserDto));

        mvc.perform(patch("/users/{userId}", testUserId)
                        .content(mapper.writeValueAsString(testUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testUserDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(testUserDto.getEmail()), String.class));

        verify(userService, times(1)).update(UserMapper.toUser(testUserDto), testUserId);
    }

    @Test
    void deleteUserShouldReturn200() throws Exception {
        mvc.perform(delete("/users/{userId}", testUserId))
                .andExpect(status().isOk());

        verify(userService, times(1)).delete(testUserId);
    }

    @Test
    void getUserShouldReturn200AndUserDto() throws Exception {
        when(userService.getById(testUserId))
                .thenReturn(UserMapper.toUser(testUserDto));

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testUserDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(testUserDto.getEmail()), String.class));

        verify(userService, times(1)).getById(testUserId);
    }

    @Test
    void getAllUsersShouldReturn200AndUserDtoList() throws Exception {

        when(userService.getAll())
                .thenReturn(List.of(UserMapper.toUser(testUserDto)));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(testUserDto))))
                .andExpect(jsonPath("$.length()", is(1)));

        verify(userService, times(1)).getAll();
    }

    @Test
    void getUserByIdWhenUserNotFoundReturnStatusNotFound() throws Exception {
        Long unknownUserId = 999L;

        when(userService.getById(anyLong()))
                .thenThrow(DataNotFoundException.class);

        mvc.perform(get("/users/{userId}", unknownUserId))
                .andExpect(status().isNotFound());

        verify(userService).getById(unknownUserId);
    }

    @Test
    void updateUserWithNotUniqueEmailReturnStatusConflict() throws Exception {
        UserDto wrongEmailUserDto = UserDto.builder()
                .id(2L)
                .name("Boris")
                .email("alexFirst@yandex.ru")
                .build();

        when(userService.getById(2L)).thenReturn(UserMapper.toUser(wrongEmailUserDto));

        when(userService.update(any(User.class), anyLong()))
                .thenThrow(EmailAlreadyExistException.class);

        mvc.perform(patch("/users/{userId}", 2L)
                        .content(mapper.writeValueAsString(wrongEmailUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(userService).update(UserMapper.toUser(wrongEmailUserDto), 2L);
    }

}
