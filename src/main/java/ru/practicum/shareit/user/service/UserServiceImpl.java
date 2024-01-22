package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

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
        repository.validateEmail(user.getEmail(), 0L);
        return repository.create(user);
    }

    @Override
    public User update(User user, Long userId) {
        checkUser(userId);
        return repository.update(user, userId);
    }

    @Override
    public void delete(Long userId) {
        checkUser(userId);
        repository.delete(userId);
    }

    @Override
    public User getById(Long userId) {
        return repository.getById(userId);
    }

    private void checkUser(Long id) {
        User user = repository.getById(id);
    }

}
