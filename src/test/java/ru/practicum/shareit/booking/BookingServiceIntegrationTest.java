package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceIntegrationTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingServiceImpl bookingService;

    private User owner = new User(0L, "owner", "owner@email.com");
    private UserDto ownerDto;
    private User booker = new User(0L, "booker", "booker@email.com");
    private UserDto bookerDto;

    private Item item1 = new Item(0L, "item1", "item1 description", true, owner, null, null, null);
    private Item item2 = new Item(0L, "item2", "item2 description", true, booker, null, null, null);
    private Item item3 = new Item(0L, "item3", "item3 description", true, booker, null, null, null);
    private ItemDto itemDto;
    private Booking booking1 = new Booking(0L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
            item1, owner, Status.APPROVED);
    private Booking booking2 = new Booking(0L, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2),
            item2, booker, Status.APPROVED);

    private final int from = 0;
    private final int size = 20;

    @BeforeEach
    void initDb() {
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item1.setOwner(owner);
        item1 = itemRepository.save(item1);
        item2.setOwner(owner);
        item2 = itemRepository.save(item2);
        item3.setOwner(booker);
        item3 = itemRepository.save(item3);
        booking1.setItem(item1);
        booking2.setItem(item2);
    }

    @AfterEach
    void clearDb() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllBookingsByBookerAllStateTest() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByBookerId(BookingState.ALL, booker.getId(), from, size);
        assertEquals(2L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsForOwnerAllStateItems() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByOwnerId(BookingState.ALL, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsByBookerCurrentStateTest() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByBookerId(BookingState.CURRENT, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsForOwnerCurrentStateItems() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByOwnerId(BookingState.CURRENT, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsByBookerPastStateTest() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByBookerId(BookingState.PAST, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsForOwnerPastStateItems() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByOwnerId(BookingState.PAST, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsByBookerFutureStateTest() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByBookerId(BookingState.FUTURE, booker.getId(), from, size);
        assertEquals(2L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsForOwnerFutureStateItems() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByOwnerId(BookingState.FUTURE, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsByBookerWaitingStateTest() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByBookerId(BookingState.WAITING, booker.getId(), from, size);
        assertEquals(2L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsForOwnerWaitingStateItems() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByOwnerId(BookingState.WAITING, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsByBookerRejectedStateTest() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByBookerId(BookingState.REJECTED, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }

    @Test
    void getAllBookingsForOwnerRejectedStateItems() {
        BookingDtoResponse bookingDto1 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking1));
        BookingDtoResponse bookingDto2 = bookingService.createBooking(booker.getId(), BookingMapper.toBookingDto(booking2));
        Collection<BookingDtoResponse> bookings = bookingService.getBookingsByOwnerId(BookingState.REJECTED, booker.getId(), from, size);
        assertEquals(0L, bookings.size());
        assertThat(bookings.containsAll(List.of(bookingDto1, bookingDto2)));
    }
}