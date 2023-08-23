package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
@Setter
@Getter
@EqualsAndHashCode
public class ItemRequestDto {
    Long id;
    String description;
    User requester;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
    List<ItemDto> items;
}
