package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
import java.util.stream.Collectors;


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
    public BookingDtoResponse approvedOrRejected(Boolean approved, long ownerId, long bookingId) {
        User user = checkUserExistAndGet(ownerId);
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
    public BookingDtoResponse getBooking(long bookingId, long userId) {
        Booking booking = checkBookingExistAndGet(bookingId);
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new NotFoundException("Пользователь " + userId + " не бронировал и не является владелецем забронированной вещи");
        }
        // log.info("Бронирование веши {} создано. Ожидает изменение статуса со стороны владельца.", item);
        return BookingMapper.toBookingDtoResponseFromBooking(booking);
    }

    @Override
    public Collection<BookingDtoResponse> getBookingsByBookerId(BookingState state, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Пользователь " + userId + " не найден");
        } else {
            return getBookingDtoList(state.toString(), userId, false);
        }
    }

    @Override
    public Collection<BookingDtoResponse> getBookingsByOwnerId(BookingState state, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь " + userId + " не найден");
        }
        if (!itemRepository.existsItemByOwnerId(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "У пользователя " + userId + " нет вещей для бронирования");
        } else {
            return getBookingDtoList(state.toString(), userId, true);
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

    private List<BookingDtoResponse> getBookingDtoList(String state, Long userId, Boolean isOwner) {
        List<Long> itemsId;
        List<BookingDtoResponse> bookingDtoList;

        switch (BookingState.checkState(state.toUpperCase())) {
            case ALL:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookingDtoList = bookingRepository
                            .findAllByItemIdInOrderByStartDesc(itemsId)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                } else {
                    bookingDtoList = bookingRepository
                            .findAllByBookerIdOrderByStartDesc(userId)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                }
                break;
            case CURRENT:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookingDtoList = bookingRepository
                            .findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    itemsId, LocalDateTime.now(), LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                } else {
                    bookingDtoList = bookingRepository
                            .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                                    userId, LocalDateTime.now(), LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                }
                break;
            case PAST:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookingDtoList = bookingRepository
                            .findAllByItemIdInAndEndIsBeforeOrderByStartDesc(itemsId, LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                } else {
                    bookingDtoList = bookingRepository
                            .findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                }
                break;
            case FUTURE:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookingDtoList = bookingRepository
                            .findAllByItemIdInAndStartIsAfterOrderByStartDesc(itemsId, LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                } else {
                    bookingDtoList = bookingRepository
                            .findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                }
                break;
            case WAITING:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookingDtoList = bookingRepository
                            .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.WAITING)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                } else {
                    bookingDtoList = bookingRepository
                            .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.WAITING)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                }
                break;
            case REJECTED:
                if (isOwner) {
                    itemsId = itemRepository.findAllItemIdByOwnerId(userId);
                    bookingDtoList = bookingRepository
                            .findAllByItemIdInAndStatusIsOrderByStartDesc(itemsId, Status.REJECTED)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                } else {
                    bookingDtoList = bookingRepository
                            .findAllByBookerIdAndStatusIsOrderByStartDesc(userId, Status.REJECTED)
                            .stream()
                            .map(BookingMapper::toBookingDtoResponseFromBooking)
                            .collect(Collectors.toList());
                }
                break;
            default:
                throw new StateException("Unknown state: " + state);
        }

        return bookingDtoList;
    }

}
