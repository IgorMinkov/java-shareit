package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();
    User add(User user);

    User update(User user, long userId);

    void delete(long userId);

    User getById(long userId);

}
