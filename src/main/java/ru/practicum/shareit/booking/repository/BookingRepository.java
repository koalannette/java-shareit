package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(Long itemId, LocalDateTime end, Status status);

    Booking findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(Long itemId, LocalDateTime start, Status status);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusIsOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findAllByItemIdInOrderByStartDesc(Collection<Long> itemId);

    List<Booking> findAllByItemIdInAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Collection<Long> itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemIdInAndEndIsBeforeOrderByStartDesc(Collection<Long> itemId, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStartIsAfterOrderByStartDesc(Collection<Long> itemId, LocalDateTime start);

    List<Booking> findAllByItemIdInAndStatusIsOrderByStartDesc(Collection<Long> itemId, Status status);

    Boolean existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(
            Long itemId, Long bookerId, Status status, LocalDateTime end);

}
