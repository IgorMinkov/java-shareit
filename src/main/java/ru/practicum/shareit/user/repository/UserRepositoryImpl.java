package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long userCounter = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        generateUserId(user);
        users.put(user.getId(), user);
        User newUser = users.get(user.getId());
        log.info("Создан пользователь: {}", newUser);
        return newUser;
    }

    @Override
    public User update(User user, Long userId) {
        User newUser = users.get(userId);
        Optional.ofNullable(user.getName()).ifPresent(newUser::setName);
        if (user.getEmail() != null) {
            validateEmail(user.getEmail(), userId);
            newUser.setEmail(user.getEmail());
        }
        users.put(userId, newUser);
        log.info("Обновлен пользователь: {}", newUser);
        return users.get(userId);
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
        log.info("Удален пользователь с id: {}", userId);
    }

    @Override
    public User getById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new DataNotFoundException(String.format("Не найден пользователь c id: %s", userId));
        }
        return users.get(userId);
    }

    @Override
    public void validateEmail(String email, Long userId) {
        boolean check = users.values().stream()
                .filter(user -> !Objects.equals(user.getId(), userId))
                .anyMatch(user -> user.getEmail().equals(email));
        if (check) {
            throw new EmailAlreadyExistException("Пользователь с таким e-mail уже существует");
        }
    }

    private void generateUserId(User user) {
        user.setId(++userCounter);
    }

}
