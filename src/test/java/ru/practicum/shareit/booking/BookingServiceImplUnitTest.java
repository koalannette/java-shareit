package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ActiveProfiles("unit-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BookingServiceImplUnitTest {

    private BookingService bookingService;

    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingServiceImpl(
                bookingRepository, userRepository, itemRepository);

        user1 = new User();
        user1.setId(1L);
        user1.setName("test name");
        user1.setEmail("test@test.ru");

        user2 = new User();
        user2.setId(2L);
        user2.setName("test name2");
        user2.setEmail("test2@test.ru");

        item1 = new Item();
        item1.setId(1L);
        item1.setName("test item");
        item1.setDescription("test item description");
        item1.setAvailable(Boolean.TRUE);
        item1.setOwner(user1);

        item2 = new Item();
        item2.setId(2L);
        item2.setName("test item2");
        item2.setDescription("test item2 description");
        item2.setAvailable(Boolean.TRUE);
        item2.setOwner(user2);

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();

        booking = BookingMapper.toBooking(bookingDto, user1, item2, Status.WAITING);
        booking.setId(1L);
    }

    @Test
    void whenCreateBookingIsSuccess() {
        BookingDtoResponse expected = BookingMapper.toBookingDtoResponseFromBooking(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item2));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoResponse actual = bookingService.createBooking(3L, bookingDto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenCreateBookingOfUserItemIsNotSuccess() {
        booking.setItem(item1);
        String expectedMessage = "Владелец вещи не может бронировать свои вещи.";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        assertThatThrownBy(() -> bookingService.createBooking(1L, bookingDto))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenApproveOrRejectedIsSuccess() {
        booking.setItem(item1);
        BookingDtoResponse expected = BookingMapper.toBookingDtoResponseFromBooking(booking);
        expected.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoResponse actual = bookingService.approvedOrRejected(true, user1.getId(), 3L);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenApproveOrRejectedByInvalidOwnerIdIsNotSuccess() {
        String expectedMessage = "Пользователь не является владельцем вещи.";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approvedOrRejected(true, user1.getId(), 3L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenApproveOrRejectedByStatusIsApprovedIsNotSuccess() {
        String expectedMessage = "Бронь с id = " + 1L + " не находится в статусе ожидания";
        booking.setItem(item1);
        booking.setStatus(Status.APPROVED);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.approvedOrRejected(true, user1.getId(), 1L))
                .isInstanceOf(NotAvailableException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetBookingIsSuccess() {
        BookingDtoResponse expected = BookingMapper.toBookingDtoResponseFromBooking(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoResponse actual = bookingService.getBooking(1L, user1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenGetBookingByInvalidUserIdIsNotSuccess() {
        booking.setBooker(user2);
        String expectedMessage = "Пользователь " + user1.getId() + " не бронировал и не является владелецем забронированной вещи";
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBooking(1L, user1.getId()))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }
}

