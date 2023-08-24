package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.BookingState;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.BookingState.UNSUPPORTED_STATUS;

@SpringBootTest
public class BookingStateTest {
    @Test
    void getEnumTest() {

        String stateStr = "Unknown";
        String finalStateStr = stateStr;
        assertEquals(UNSUPPORTED_STATUS, BookingState.checkState(finalStateStr));

        stateStr = "ALL";
        BookingState stateTest = BookingState.checkState(stateStr);
        assertEquals(stateTest, BookingState.ALL);

        stateStr = "CURRENT";
        stateTest = BookingState.checkState(stateStr);
        assertEquals(stateTest, BookingState.CURRENT);

        stateStr = "PAST";
        stateTest = BookingState.checkState(stateStr);
        assertEquals(stateTest, BookingState.PAST);

        stateStr = "FUTURE";
        stateTest = BookingState.checkState(stateStr);
        assertEquals(stateTest, BookingState.FUTURE);

        stateStr = "REJECTED";
        stateTest = BookingState.checkState(stateStr);
        assertEquals(stateTest, BookingState.REJECTED);

        stateStr = "WAITING";
        stateTest = BookingState.checkState(stateStr);
        assertEquals(stateTest, BookingState.WAITING);
    }
}
