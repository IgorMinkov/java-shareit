package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    User create(User user);

    User update(User user, Long userId);

    void delete(Long userId);

    User getById(Long userId);

}
