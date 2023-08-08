package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Date;

@Data
@Builder
public class Booking {
    private Long bookingId;
    private Date start;
    private Date end;
    private Item item;
    private User booker;
    private boolean status;

}
