package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {

    BookingDtoResponse createBooking(Long userId, BookingDto bookingDto);

    BookingDtoResponse approvedOrRejected(Boolean approved, long ownerId, long bookingId);

    BookingDtoResponse getBooking(long bookingId, long userId);

    Collection<BookingDtoResponse> getBookingsByBookerId(BookingState state, long userId);

    Collection<BookingDtoResponse> getBookingsByOwnerId(BookingState state, long userId);
}
