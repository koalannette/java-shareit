package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSmallDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestSmallDto itemRequest1SmallDto;

    private ItemRequestSmallDto itemRequest2SmallDto;

    private ItemRequestDto itemRequest1Dto;

    private ItemRequestDto itemRequest2Dto;

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {

        itemRequest1SmallDto = ItemRequestSmallDto.builder()
                .description("ItemRequest 1")
                .build();

        itemRequest2SmallDto = ItemRequestSmallDto.builder()
                .description("ItemRequest 2")
                .build();

        itemRequest1Dto = ItemRequestDto.builder()
                //.id(1L)
                .description("ItemRequest 1")
                .created(LocalDateTime.now())
                .build();

        itemRequest2Dto = ItemRequestDto.builder()
                .id(2L)
                .description("ItemRequest 2")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(anyLong(), any(ItemRequestSmallDto.class))).thenReturn(itemRequest1Dto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequest1SmallDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.id", is(itemRequest1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest1SmallDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).createItemRequest(1L, itemRequest1SmallDto);
    }

    @Test
    void getRequests() throws Exception {
        when(itemRequestService.getRequestsByOwner(anyLong())).thenReturn(List.of(itemRequest1Dto, itemRequest2Dto));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest1Dto, itemRequest2Dto))));

        verify(itemRequestService, times(1)).getRequestsByOwner(1L);
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequest1Dto, itemRequest2Dto));

        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequest1Dto, itemRequest2Dto))));

        verify(itemRequestService, times(1)).getAllRequests(1L, 0, 10);
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequest1Dto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .content(mapper.writeValueAsString(itemRequest1SmallDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.id", is(itemRequest1SmallDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequest1SmallDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).getRequestById(1L, 1L);
    }
}