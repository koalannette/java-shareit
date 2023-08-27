package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse createBooking(Long bookerId, BookingDto bookingDto) {
        User booker = checkUserExistAndGet(bookerId);
        Item item = checkItemExistAndGet(bookingDto.getItemId());

        if (booker.getId().equals(item.getOwner().getId())) {
            throw new NotFoundException("Владелец вещи не может бронировать свои вещи.");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, booker, item, Status.WAITING);
        log.info("Бронирование веши {} создано. Ожидает изменение статуса со стороны владельца.", item);
        return BookingMapper.toBookingDtoResponseFromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse approvedOrRejected(Boolean approved, Long ownerId, Long bookingId) {
        checkUserExistAndGet(ownerId);
        Booking booking = checkBookingExistAndGet(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи.");
        }
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new NotAvailableException("Бронь с id = " + bookingId + " не находится в статусе ожидания");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return BookingMapper.toBookingDtoResponseFromBooking(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse getBooking(Long bookingId, Long userId) {
        Booking booking = checkBookingExistAndGet(bookingId);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("Пользователь " + userId + " не бронировал и не является владелецем забронированной вещи");
        }
        log.info("Отправлены данные о бронирование веши {} .", booking);
        return BookingMapper.toBookingDtoResponseFromBooking(booking);
    }

    @Override
    public Collection<BookingDtoResponse> getBookingsByBookerId(BookingState state, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        } else {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            Page<Booking> bookings = getBookingPage(state.toString(), userId, false, pageRequest);
            return BookingMapper.toBookingDtoList(bookings);
        }
    }

    @Override
    public Collection<BookingDtoResponse> getBookingsByOwnerId(BookingState state, Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь " + userId + " не найден");
        }
        if (!itemRepository.existsItemByOwnerId(userId)) {
            throw new NotFoundException("У пользователя " + userId + " нет вещей для бронирования");
        } else {
            PageRequest pageRequest = PageRequest.of(from / size, size);
            Page<Booking> bookings = getBookingPage(state.toString(), userId, true, pageRequest);
            return BookingMapper.toBookingDtoList(bookings);
        }
    }

    private Item checkItemExistAndGet(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с id = " + itemId + " не найдена."));
        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь недоступна.");
        }
        return item;
    }

    private User checkUserExistAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден."));
    }

    private Booking checkBookingExistAndGet(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронь с id = " + bookingId + " не найден."));
    }

    private Page<Booking> getBookingPage(String state, Long userId, Boolean isOwner, PageRequest pageRequest) {
        List<Long> itemsId;
        Page<Booking> bookings = null;

        switch (BookingState.checkState(state.toUpperCase())) {
            case ALL:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookings = bookingRepository
                            .findAllByItemIdInOrderByStartDesc(itemsId, pageRequest);
                } else {
                    bookings = bookingRepository
                            .findAllByBookerIdOrderByStartDesc(userId, pageRequest);
                }
                break;
            case CURRENT:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookings = bookingRepository
                            .findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    itemsId, LocalDateTime.now(), LocalDateTime.now(), pageRequest)
                    ;
                } else {
                    bookings = bookingRepository
                            .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    userId, LocalDateTime.now(), LocalDateTime.now(), pageRequest)
                    ;
                }
                break;
            case PAST:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookings = bookingRepository
                            .findAllByItemIdInAndEndIsBeforeOrderByStartDesc(itemsId, LocalDateTime.now(), pageRequest)
                    ;
                } else {
                    bookings = bookingRepository
                            .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(), pageRequest)
                    ;
                }
                break;
            case FUTURE:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookings = bookingRepository
                            .findAllByItemIdInAndStartIsAfterOrderByStartDesc(itemsId, LocalDateTime.now(), pageRequest)
                    ;
                } else {
                    bookings = bookingRepository
                            .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(), pageRequest)
                    ;
                }
                break;
            case WAITING:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookings = bookingRepository
                            .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.WAITING, pageRequest)
                    ;
                } else {
                    bookings = bookingRepository
                            .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING, pageRequest)
                    ;
                }
                break;
            case REJECTED:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookings = bookingRepository
                            .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.REJECTED, pageRequest)
                    ;
                } else {
                    bookings = bookingRepository
                            .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED, pageRequest)
                    ;
                }
                break;
            default:
                throw new StateException("Unknown state: " + state);
        }

        return bookings;
    }

}
