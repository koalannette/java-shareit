package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;
    private UserDto user1Dto;

    private UserDto user2Dto;

    @BeforeEach
    void beforeEach() {

        user1Dto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alex@yandex.ru")
                .build();

        user2Dto = UserDto.builder()
                .id(2L)
                .name("Alexander")
                .email("Alexander@yandex.ru")
                .build();
    }

    @Test
    void addUserTest() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(user1Dto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1Dto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user1Dto.getEmail()), String.class));

        verify(userService, times(1)).createUser(user1Dto);
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.editUser(anyLong(), any(UserDto.class))).thenReturn(user1Dto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(user1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1Dto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user1Dto.getEmail()), String.class));

        verify(userService, times(1)).editUser(1L, user1Dto);
    }

    @Test
    void getUsers() throws Exception {

        when(userService.getUsers()).thenReturn(List.of(user1Dto, user2Dto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(user1Dto, user2Dto))));

        verify(userService, times(1)).getUsers();
    }


    @Test
    void getUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user1Dto);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1Dto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(user1Dto.getEmail()), String.class));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(1L);
    }


}

