package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public User update(User user, long userId) {
        return null;
    }

    @Override
    public void delete(long userId) {

    }

    @Override
    public User getById(long userId) {
        return null;
    }
}
