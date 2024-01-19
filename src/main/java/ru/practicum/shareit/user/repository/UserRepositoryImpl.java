package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        log.info("Создан пользователь: {}", user);
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        users.remove(userId);
        users.put(userId, user);
        log.info("Обновлен пользователь: {}", user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        users.remove(userId);
        log.info("Удален пользователь с id: {}", userId);
    }

    @Override
    public User getById(Long userId) {
        if(!users.containsKey(userId)) {
            throw new DataNotFoundException(String.format("Не найден пользователь c id: %s", userId));
        }
        return users.get(userId);
    }

    @Override
    public void validateEmail(String email) {
        boolean check = users.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if(check) {
            throw new EmailAlreadyExistException("Пользователь с таким e-mail уже существует");
        }
    }

    private void generateUserId(User user) {
        user.setId(++userCounter);
    }

}
