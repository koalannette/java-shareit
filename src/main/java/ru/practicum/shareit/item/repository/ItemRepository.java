package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item save(Item item);

    Item updateItem(Item updateItem, Long itemId, Long ownerId);

    Item getItemById(Long itemId);

    List<Item> getItemsByUserId(Long id);

    List<Item> getItemsByText(String text);

}
