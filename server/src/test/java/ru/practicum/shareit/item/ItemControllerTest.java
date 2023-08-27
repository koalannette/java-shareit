package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto item1Dto;

    private ItemDto item2Dto;

    private ItemDtoResponse item1DtoResponse;

    private ItemDtoResponse item2DtoResponse;

    private CommentDto commentDto;
    private CommentDtoResponse commentDtoResponse;

    private ItemRequest itemRequest;

    private User user;

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {

        user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Хотел бы воспользоваться молотком")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Хороший молоток")
                .created(LocalDateTime.now())
                .author(user.getName())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Хороший молоток")
                .created(LocalDateTime.now())
                .author(user.getName())
                .build();

        commentDtoResponse = CommentDtoResponse.builder()
                .id(1L)
                .text("Хороший молоток")
                .created(LocalDateTime.now())
                .authorName(user.getName())
                .build();

        item1Dto = ItemDto.builder()
                .id(1L)
                .name("Молоток")
                .description("Забивает гвозди")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        item2Dto = ItemDto.builder()
                .id(1L)
                .name("guitar")
                .description("a very good tool")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        item1DtoResponse = ItemDtoResponse.builder()
                .id(1L)
                .name("Молоток")
                .description("Забивает гвозди")
                .available(true)
                //.comments(List.of(commentDto))
                .requestId(itemRequest.getId())
                .build();

        item2DtoResponse = ItemDtoResponse.builder()
                .id(1L)
                .name("Молоток")
                .description("Забивает гвозди")
                .available(true)
                //.comments(List.of(commentDto))
                .requestId(itemRequest.getId())
                .build();
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.createItem(anyLong(), any(ItemDto.class))).thenReturn(item1DtoResponse);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1Dto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(item1Dto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(item1Dto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(item1Dto.getRequestId()), Long.class));

        verify(itemService, times(1)).createItem(1L, item1Dto);
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(any(ItemDto.class), anyLong(), anyLong())).thenReturn(item1DtoResponse);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(item1Dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1Dto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(item1Dto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(item1Dto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(item1Dto.getRequestId()), Long.class));

        verify(itemService, times(1)).updateItem(item1Dto, 1L, 1L);
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(item1DtoResponse);

        mvc.perform(get("/items/{itemId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item1Dto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(item1Dto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(item1Dto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(item1Dto.getRequestId()), Long.class));

        verify(itemService, times(1)).getItemById(1L, 1L);
    }

    @Test
    void getAllItemsUser() throws Exception {

        when(itemService.getItemsByUserId(anyLong(), anyInt(), anyInt())).thenReturn(List.of(item1DtoResponse, item2DtoResponse));

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item1DtoResponse, item2DtoResponse))));

        verify(itemService, times(1)).getItemsByUserId(1L, 0, 10);
    }

    @Test
    void getSearchItem() throws Exception {
        when(itemService.getItemsByText(anyString(), anyInt(), anyInt())).thenReturn(List.of(item1DtoResponse, item2DtoResponse));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(item1DtoResponse, item2DtoResponse))));

        verify(itemService, times(1)).getItemsByText("text", 0, 10);
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(any(CommentDto.class), anyLong(), anyLong())).thenReturn(commentDtoResponse);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentDtoResponse)));

        //verify(itemService, times(1)).addComment(commentDto, 1L, 1L);
    }
}
