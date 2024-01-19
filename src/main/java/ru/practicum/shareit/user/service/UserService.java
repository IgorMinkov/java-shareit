package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {

    List<User> getAll();
    User add(User user);

    User update(User user, long userId);

    void delete(long userId);

    User getById(long userId);

}
