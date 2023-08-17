package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {

    User save(User user);

    User updateUser(User user, Long id);

    List<User> findAll();

    User findUserById(Long id);

    void deleteUser(Long id);

    void checkUser(Long id);
}
