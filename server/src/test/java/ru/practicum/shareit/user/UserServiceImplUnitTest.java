package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ActiveProfiles("unit-test")
@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    public void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("name1")
                .email("test1@test.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("name2")
                .email("test2@test.ru")
                .build();

        userDto1 = UserMapper.toUserDto(user1);

        userDto2 = UserMapper.toUserDto(user2);
    }

    @Test
    void whenCreateUser() {
        when(userRepository.save(any())).thenReturn(user1);

        UserDto actual = userService.createUser(userDto1);

        assertThat(actual).usingRecursiveComparison().isEqualTo(userDto1);
    }

    @Test
    void whenEditUserIsSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenReturn(user2);

        UserDto actual = userService.editUser(user1.getId(), userDto2);
        userDto2.setId(user1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(userDto2);
    }

    @Test
    void whenEditUserByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не найден";

        assertThatThrownBy(() -> userService.editUser(999L, userDto2))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> actual = userService.getUsers();

        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(userDto1, userDto2));
    }

    @Test
    void whenGetUserByIdIsSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        UserDto actual = userService.getUserById(user1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(userDto1);
    }

    @Test
    void whenGetUserByIdIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не найден";

        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }
}
