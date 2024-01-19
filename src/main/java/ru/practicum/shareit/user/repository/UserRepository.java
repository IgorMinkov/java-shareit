package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User create(User user);

    User update(User user, Long userId);

    void delete(Long userId);

    User getById(Long userId);

    void validateEmail(String email);

}
