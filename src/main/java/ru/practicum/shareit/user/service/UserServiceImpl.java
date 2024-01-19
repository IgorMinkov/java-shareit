package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

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
