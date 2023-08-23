package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class BookingRepositoryTest {

    private final TestEntityManager entityManager;
    private final BookingRepository bookingRepository;

    private final PageRequest pageRequest = PageRequest.of(0, 3);
    private User user;
    private Item item;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .name("name")
                .build();

        item = Item.builder()
                .name("name")
                .available(true)
                .description("desc")
                .owner(user)
                .build();

        booking1 = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(20))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        booking2 = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(20))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();

        booking3 = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(20))
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Test
    void whenFindFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc() {
        booking1.setStart(LocalDateTime.now().plusHours(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        Booking expected = booking1;

        Booking actual = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(item.getId(), LocalDateTime.now().plusDays(2), Status.WAITING);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindFirstByItemIdAndStartAfterAndStatusOrderByStartAsc() {
        booking1.setStart(LocalDateTime.now().plusHours(10));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        Booking expected = booking1;

        Booking actual = bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(item.getId(), LocalDateTime.now(), Status.WAITING);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDescPageable() {
        booking1.setStart(LocalDateTime.now().plusHours(3));
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking1.setEnd(LocalDateTime.now().plusHours(9));
        booking2.setEnd(LocalDateTime.now().plusHours(10));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(List.of(item.getId()),
                LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(7), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInAndEndIsBeforeOrderByStartDescPageable() {
        booking1.setEnd(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(2));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByItemIdInAndEndIsBeforeOrderByStartDesc(List.of(item.getId()),
                LocalDateTime.now().plusHours(7), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInAndStartIsAfterOrderByStartDescPageable() {
        booking1.setStart(LocalDateTime.now().plusHours(5));
        booking2.setStart(LocalDateTime.now().plusHours(4));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByItemIdInAndStartIsAfterOrderByStartDesc(List.of(item.getId()),
                LocalDateTime.now().plusHours(2), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInOrderByStartDescPageable() {
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        Page<Booking> expected = new PageImpl(List.of(booking1, booking2, booking3));

        Page<Booking> actual = bookingRepository.findAllByItemIdInOrderByStartDesc(List.of(item.getId()), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInAndStatusIsOrderByStartDescPageable() {
        booking3.setStatus(Status.APPROVED);
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        Page<Booking> expected = new PageImpl(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByItemIdInAndStatusIsOrderByStartDesc(
                List.of(item.getId()), Status.WAITING, pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdOrderByStartDescPageable() {
        booking1.setStart(LocalDateTime.now().plusHours(3));
        booking2.setStart(LocalDateTime.now().plusHours(2));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId(), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDescPageable() {
        booking1.setStart(LocalDateTime.now().plusHours(8));
        booking2.setStart(LocalDateTime.now().plusHours(7));
        booking1.setEnd(LocalDateTime.now().plusHours(14));
        booking2.setEnd(LocalDateTime.now().plusHours(13));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(12), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndEndIsBeforeOrderByStartDescPageable() {
        booking1.setEnd(LocalDateTime.now().plusHours(5));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusHours(10), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStartIsAfterOrderByStartDescPageable() {
        booking1.setStart(LocalDateTime.now().plusHours(5));
        booking2.setStart(LocalDateTime.now().plusHours(4));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusHours(2), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStatusIsOrderByStartDescPageable() {
        booking3.setStatus(Status.APPROVED);
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        Page<Booking> expected = new PageImpl<>(List.of(booking1, booking2));

        Page<Booking> actual = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(user.getId(),
                Status.WAITING, pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdOrderByStartDesc() {
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2, booking3);

        List<Booking> actual = bookingRepository.findAllByBookerIdOrderByStartDesc(user.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusHours(8));
        booking2.setStart(LocalDateTime.now().plusHours(7));
        booking1.setEnd(LocalDateTime.now().plusHours(14));
        booking2.setEnd(LocalDateTime.now().plusHours(13));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(12));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndEndIsBeforeOrderByStartDesc() {
        booking1.setEnd(LocalDateTime.now().plusHours(5));
        booking2.setEnd(LocalDateTime.now().plusHours(4));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusHours(10));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStartIsAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusHours(5));
        booking2.setStart(LocalDateTime.now().plusHours(4));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(user.getId(),
                LocalDateTime.now().plusHours(2));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByBookerIdAndStatusIsOrderByStartDesc() {
        booking3.setStatus(Status.APPROVED);
        booking1.setStart(LocalDateTime.now().plusHours(2));
        booking2.setStart(LocalDateTime.now().plusHours(1));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByBookerIdAndStatusIsOrderByStartDesc(user.getId(),
                Status.WAITING);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInOrderByStartDesc() {
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2, booking3);

        List<Booking> actual = bookingRepository.findAllByItemIdInOrderByStartDesc(List.of(item.getId()));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInAndStatusIsOrderByStartDesc() {
        booking3.setStatus(Status.APPROVED);
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.persist(booking3);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemIdInAndStatusIsOrderByStartDesc(
                List.of(item.getId()), Status.WAITING);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenExistsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore() {
        booking1.setEnd(LocalDateTime.now().plusHours(8));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);

        Boolean actual = bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(
                item.getId(), user.getId(), Status.WAITING, LocalDateTime.now().plusHours(10));

        assertThat(actual).isTrue();
    }

    @Test
    void whenFindAllByItemIdInAndStartIsAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusHours(5));
        booking2.setStart(LocalDateTime.now().plusHours(4));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemIdInAndStartIsAfterOrderByStartDesc(List.of(item.getId()),
                LocalDateTime.now().plusHours(2));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInAndEndIsBeforeOrderByStartDesc() {
        booking1.setEnd(LocalDateTime.now().plusHours(3));
        booking2.setEnd(LocalDateTime.now().plusHours(2));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemIdInAndEndIsBeforeOrderByStartDesc(List.of(item.getId()),
                LocalDateTime.now().plusHours(7));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc() {
        booking1.setStart(LocalDateTime.now().plusHours(3));
        booking2.setStart(LocalDateTime.now().plusHours(2));
        booking1.setEnd(LocalDateTime.now().plusHours(9));
        booking2.setEnd(LocalDateTime.now().plusHours(10));
        entityManager.persist(user);
        entityManager.persist(item);
        entityManager.persist(booking1);
        entityManager.persist(booking2);
        List<Booking> expected = List.of(booking1, booking2);

        List<Booking> actual = bookingRepository.findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(List.of(item.getId()),
                LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(7));

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
