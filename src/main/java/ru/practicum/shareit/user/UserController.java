package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.create(mapper.toUser(userDto));
        log.info("Добавлен пользователь: {} ", user);
        return mapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @Positive @PathVariable Long userId) {
        User user = mapper.toUser(userDto);
        User updatedUser = userService.update(user, userId);
        log.info("Обновлен пользователь: {} ", updatedUser);
        return mapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@Positive @PathVariable Long userId) {
        userService.delete(userId);
        log.info("Пользователь с id: {} удален", userId);
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@Positive @PathVariable Long userId) {
        User user = userService.getById(userId);
        log.info("Найден пользователь по id: {} ", user);
        return mapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList());
    }

}
