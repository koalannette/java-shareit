package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDtoResponse createBooking(Long userId, BookingDto bookingDto);

    BookingDtoResponse approvedOrRejected(Boolean approved, Long ownerId, Long bookingId);

    BookingDtoResponse getBooking(Long bookingId, Long userId);

    Collection<BookingDtoResponse> getBookingsByBookerId(BookingState state, Long userId, Integer from, Integer size);

    Collection<BookingDtoResponse> getBookingsByOwnerId(BookingState state, Long userId, Integer from, Integer size);
}
