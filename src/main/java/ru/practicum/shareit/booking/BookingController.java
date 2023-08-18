package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotAvailableException;

import javax.validation.Valid;
import java.util.Collection;

import static ru.practicum.shareit.booking.model.BookingState.ALL;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(REQUEST_HEADER) Long bookerId,
                                            @RequestBody @Valid BookingDto bookingDto) {
        log.info("Поступил запрос на бронирование вещи пользователем с id = {} .", bookerId);
        if (bookingDto.getStart().equals(bookingDto.getEnd()) || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new NotAvailableException("Начало бронирования не может быть равно или или быть позже окончанию бронирования");
        }
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approvedOrRejected(@RequestParam(name = "approved") Boolean approved,
                                                 @RequestHeader(REQUEST_HEADER) long ownerId,
                                                 @PathVariable("bookingId") Long bookingId) {
        log.info("Подтверждение или отклонение запроса на бронировании");
        return bookingService.approvedOrRejected(approved, ownerId, bookingId);
    }


    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long bookingId) {
        log.info("Получение данных о бронировании по id");
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(required = false) BookingState state) {
        if (state == null) {
            state = ALL;
        }
        log.info("Получение списка всех бронирований пользователя по его id");
        return bookingService.getBookingsByBookerId(state, userId);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                               @RequestParam(defaultValue = "ALL") BookingState state) {
        log.info("Получение списка бронирований для всех вещей пользователя по его id");
        return bookingService.getBookingsByOwnerId(state, ownerId);
    }

}
