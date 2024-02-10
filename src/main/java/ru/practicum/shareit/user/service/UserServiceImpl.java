package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<User> getAll() {
        return repository.findAll();
    }

    @Override
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    public User update(User user, Long userId) {
        User newUser = getById(userId);
        Optional.ofNullable(user.getName()).ifPresent(newUser::setName);
        if (user.getEmail() != null) {
            validateEmail(user.getEmail(), userId);
            newUser.setEmail(user.getEmail());
        }
        repository.save(newUser);
        return newUser;
    }

    @Override
    public void delete(Long userId) {
        checkUser(userId);
        repository.deleteById(userId);
    }

    @Override
    public User getById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Не найден пользователь c id: %s", userId)));
    }

    @Override
    public void checkUser(Long id) {
        if(!repository.existsById(id)) {
            throw new DataNotFoundException(String.format("Не найден пользователь c id: %s", id));
        }
    }

    private void validateEmail(String email, Long userId) {
        boolean check = repository.findByEmail(email).stream()
                .filter(user -> !Objects.equals(user.getId(), userId))
                .anyMatch(user -> user.getEmail().equals(email));
        if (check) {
            throw new EmailAlreadyExistException("Пользователь с таким e-mail уже существует");
        }
    }

}
