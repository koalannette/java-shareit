package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto editUser(Long userId, UserDto userDto);

    List<UserDto> getUsers();

    UserDto getUserById(Long id);

    void deleteUserById(Long id);
}
