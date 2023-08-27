package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.interfaces.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public UserDto createUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("SERVER: Поступил запрос на добавление пользователя.");
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto editUser(@PathVariable("userId") Long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("SERVER: Поступил запрос на изменения пользователя.");
        return userService.editUser(id, userDto);
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("SERVER: Запрос всех пользователей");
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("SERVER: Поступил запрос на получение пользователя");
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.info("SERVER: Поступил запрос на удаление пользователя");
        userService.deleteUserById(id);
    }
}
