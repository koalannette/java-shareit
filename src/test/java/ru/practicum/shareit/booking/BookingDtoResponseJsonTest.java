package ru.practicum.shareit.booking;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoResponseJsonTest {

    @Autowired
    private JacksonTester<BookingDtoResponse> jsonBookingDtoResponse;

    @Test
    @SneakyThrows
    void bookingDtoResponseTest() {
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        JsonContent<BookingDtoResponse> result = jsonBookingDtoResponse.write(bookingDtoResponse);

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
    }

}

