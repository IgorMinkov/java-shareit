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
        return repository.getAll();
    }

    @Override
    public User create(User user) {
        return repository.create(user);
    }

    @Override
    public User update(User user, Long userId) {
        return repository.update(user, userId);
    }

    @Override
    public void delete(Long userId) {
        repository.delete(userId);
    }

    @Override
    public User getById(Long userId) {
        return repository.getById(userId);
    }

}
