package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(Long ownerId, ItemDto itemDto) {
        userRepository.checkUser(ownerId);
        Item item = itemRepository.save(ItemMapper.toItem(itemDto));
        item.setOwnerId(ownerId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        if (!itemRepository.getItemById(itemId).getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не является владельцем");
        }
        Item item = itemRepository.updateItem(ItemMapper.toItem(itemDto), itemId, ownerId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        if (itemId < 0 || itemRepository.getItemById(itemId) == null) {
            throw new NotFoundException("Вещь не найдена.");

        }
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long id) {
        if (id < 0 || userRepository.findUserById(id) == null) {
            throw new NotFoundException("Пользователь не найден.");

        }
        return ItemMapper.toItemDtoList(itemRepository.getItemsByUserId(id));
    }

    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.getItemsByText(text));
    }


}
