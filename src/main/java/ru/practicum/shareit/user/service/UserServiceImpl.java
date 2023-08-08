package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto editUser(Long id, UserDto userDto) {
        User updateUser = userRepository.updateUser(UserMapper.toUser(userDto), id);
        log.info("Пользователь {} обновлен.", updateUser);
        return UserMapper.toUserDto(updateUser);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDtoList(users);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findUserById(id);
        return UserMapper.toUserDto(user);
    }


    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteUser(id);
        log.info("Пользователь с id = {} успешно удалён.", id);
    }

}
