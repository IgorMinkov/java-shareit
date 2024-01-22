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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        User user = userService.create(UserMapper.toUser(userDto));
        log.info("Добавлен пользователь: {} ", user);
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(
            @RequestBody UserDto userDto,
            @Positive @PathVariable Long userId
    ) {
        User updatedUser = userService.update(UserMapper.toUser(userDto), userId);
        log.info("Обновлен пользователь: {} ", updatedUser);
        return UserMapper.toUserDto(updatedUser);
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
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

}
