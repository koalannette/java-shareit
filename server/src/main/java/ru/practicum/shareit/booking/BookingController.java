package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(REQUEST_HEADER) Long bookerId,
                                            @RequestBody BookingDto bookingDto) {
        log.info("SERVER: Поступил запрос на бронирование вещи пользователем с id = {} .", bookerId);
        if (bookingDto.getStart().equals(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new NotAvailableException("Начало бронирования не может быть равно или или быть позже окончанию бронирования");
        }
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approvedOrRejected(@RequestParam(name = "approved") Boolean approved,
                                                 @RequestHeader(REQUEST_HEADER) Long ownerId,
                                                 @PathVariable("bookingId") Long bookingId) {
        log.info("SERVER: Подтверждение или отклонение запроса на бронировании");
        return bookingService.approvedOrRejected(approved, ownerId, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @PathVariable long bookingId) {
        log.info("SERVER: Получение данных о бронировании по id");
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> getBookingsByBookerId(@RequestHeader(REQUEST_HEADER) Long userId,
                                                                @RequestParam(defaultValue = "ALL") BookingState state,
                                                                @RequestParam(defaultValue = "0") Integer from,
                                                                @RequestParam(defaultValue = "10") Integer size) {
        log.info("SERVER: Получение списка всех бронирований пользователя по его id");
        return bookingService.getBookingsByBookerId(state, userId, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getBookingsByOwnerId(@RequestHeader(REQUEST_HEADER) Long ownerId,
                                                               @RequestParam(defaultValue = "ALL") BookingState state,
                                                               @RequestParam(defaultValue = "0") Integer from,
                                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("SERVER: Получение списка бронирований для всех вещей пользователя по его id");
        return bookingService.getBookingsByOwnerId(state, ownerId, from, size);
    }

}
