package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        User user = userService.create(UserMapper.toUser(userDto));
        log.info("Добавлен пользователь: {} ", user);
        return ResponseEntity.ok(UserMapper.toUserDto(user));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UserDto userDto,
            @PathVariable Long userId) {
        User updatedUser = userService.update(UserMapper.toUser(userDto), userId);
        log.info("Обновлен пользователь: {} ", updatedUser);
        return ResponseEntity.ok(UserMapper.toUserDto(updatedUser));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
        log.info("Пользователь с id: {} удален", userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long userId) {
        User user = userService.getById(userId);
        log.info("Найден пользователь по id: {} ", user);
        return ResponseEntity.ok(UserMapper.toUserDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList()));
    }

}
