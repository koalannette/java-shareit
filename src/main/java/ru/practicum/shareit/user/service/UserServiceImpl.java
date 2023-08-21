package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional//(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto editUser(Long id, UserDto userDto) {
        User updateUser = checkUserExistAndGet(id);

        updateUser.setName(userDto.getName() != null ? userDto.getName() : updateUser.getName());

        String oldEmail = updateUser.getEmail();
        if (!oldEmail.equals(userDto.getEmail())) {
            updateUser.setEmail(userDto.getEmail() != null ? userDto.getEmail() : oldEmail);
        }
        userRepository.save(updateUser);
        log.info("Пользователь с id = {} успешно обновлён.", id);
        return UserMapper.toUserDto(updateUser);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.toUserDtoList(users);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = checkUserExistAndGet(id);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        User user = checkUserExistAndGet(id);
        userRepository.delete(user);
        log.info("Пользователь с id = {} успешно удалён.", id);
    }

    private User checkUserExistAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

}
