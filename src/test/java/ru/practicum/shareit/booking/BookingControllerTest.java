package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private Item item;

    private User user;

    private BookingDto bookingDto;

    private BookingDtoResponse bookindDtoResponse1Dto;

    private BookingDtoResponse bookindDtoResponse2Dto;

    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {

        user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alex@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("screwdriver")
                .description("works well, does not ask to eat")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.of(2023, 9, 4, 0, 0))
                .end(LocalDateTime.of(2023, 9, 4, 12, 0))
                .build();

        bookindDtoResponse1Dto = BookingDtoResponse.builder()
                //.id(1L)
                .start(LocalDateTime.of(2023, 9, 4, 0, 0))
                .end(LocalDateTime.of(2023, 9, 4, 12, 0))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        bookindDtoResponse2Dto = BookingDtoResponse.builder()
                //.id(2L)
                .start(LocalDateTime.of(2023, 9, 4, 14, 0))
                .end(LocalDateTime.of(2023, 9, 4, 16, 0))
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.createBooking(anyLong(), any(BookingDto.class))).thenReturn(bookindDtoResponse1Dto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookindDtoResponse1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookindDtoResponse1Dto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookindDtoResponse1Dto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookindDtoResponse1Dto.getItem().getId()), Long.class));

        //verify(bookingService, times(1)).createBooking(1L, bookingDto);
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approvedOrRejected(anyBoolean(), anyLong(), anyLong())).thenReturn(bookindDtoResponse1Dto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookindDtoResponse1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookindDtoResponse1Dto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookindDtoResponse1Dto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookindDtoResponse1Dto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).approvedOrRejected(true, 1L, 1L);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookindDtoResponse1Dto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookindDtoResponse1Dto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookindDtoResponse1Dto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookindDtoResponse1Dto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookindDtoResponse1Dto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).getBooking(1L, 1L);
    }

    @Test
    void getAllBookingsByBookerId() throws Exception {
        when(bookingService.getBookingsByBookerId(any(BookingState.class), anyLong(), anyInt(), anyInt())).thenReturn(List.of(bookindDtoResponse1Dto, bookindDtoResponse2Dto));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookindDtoResponse1Dto, bookindDtoResponse2Dto))));

        verify(bookingService, times(1)).getBookingsByBookerId(BookingState.ALL, 1L, 0, 10);
    }

    @Test
    void getAllBookingsForAllItemsByOwnerId() throws Exception {
        when(bookingService.getBookingsByOwnerId(any(BookingState.class), anyLong(), anyInt(), anyInt())).thenReturn(List.of(bookindDtoResponse1Dto, bookindDtoResponse2Dto));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header(REQUEST_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookindDtoResponse1Dto, bookindDtoResponse2Dto))));

        verify(bookingService, times(1)).getBookingsByOwnerId(BookingState.ALL, 1L, 0, 10);
    }
}
