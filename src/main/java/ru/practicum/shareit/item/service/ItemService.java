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

    List<ItemDtoResponse> getItemsByUserId(Long userId, Integer from, Integer size);

    List<ItemDtoResponse> getItemsByText(String text, Integer from, Integer size);

    CommentDtoResponse addComment(CommentDto dto, Long itemId, Long userId);

}
