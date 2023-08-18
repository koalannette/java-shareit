package ru.practicum.shareit.item.service;


import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {

    ItemDtoResponse createItem(Long itemId, ItemDto itemDto);

    ItemDtoResponse updateItem(ItemDto itemDto, Long itemId, Long ownerId);

    ItemDtoResponse getItemById(Long ownerId, Long itemId);

    List<ItemDtoResponse> getItemsByUserId(Long userId);

    List<ItemDtoResponse> getItemsByText(String text);

    CommentDtoResponse addComment(CommentDto dto, Long itemId, Long userId);

}
