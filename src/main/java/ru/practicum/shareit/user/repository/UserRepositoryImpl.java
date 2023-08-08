package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserUniqueEmailException;
import ru.practicum.shareit.user.User;

import java.util.*;


@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final List<String> emails = new ArrayList<>();
    private Long userId = 0L;

    private Long getIdForUser() {
        return ++userId;
    }

    @Override
    public User save(User user) {
        checkEmailUnique(user);

        if (user.getId() == null) {
            user.setId(getIdForUser());
        }

        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно создан.", user.getId());
        return user;
    }


    @Override
    public User updateUser(User user, Long id) {

        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }

        User updateUser = users.get(id); //какого юзера будем обновлять

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!user.getEmail().equals(updateUser.getEmail())) {
                checkEmailUnique(user);
            }
        }
        checkUser(id);
        if (user.getName() != null && !user.getName().isBlank()) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            updateUser.setEmail(user.getEmail());
        }

        return users.get(id);
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(Long id) {
        return (users.get(id));
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        users.remove(id);
    }


    private void checkEmailUnique(User user) {
        for (User checkUser : users.values()) {
            if (checkUser.getEmail().equals(user.getEmail())) {
                throw new UserUniqueEmailException("Пользователь с такой почтой уже существует.");
            }
        }
    }

    @Override
    public void checkUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

}
