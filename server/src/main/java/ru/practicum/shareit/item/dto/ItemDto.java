package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    private Booking lastBooking;
    private Booking nextBooking;
    private Long requestId;
}

