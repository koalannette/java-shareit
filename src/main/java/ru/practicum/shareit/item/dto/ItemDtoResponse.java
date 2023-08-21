package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;

import java.util.Set;

@Builder
@Getter
@Setter
@ToString
public class ItemDtoResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingBookerDto lastBooking;
    private BookingBookerDto nextBooking;
    private Set<CommentDtoResponse> comments;
}