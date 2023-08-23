package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSmallDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestSmallDto itemRequestDto) {
        User user = checkUserExistAndGet(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user, List.of(), LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), List.of());
    }

    @Override
    public List<ItemRequestDto> getRequestsByOwner(Long ownerId) {
        checkUserExistAndGet(ownerId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(ownerId);
        List<ItemRequestDto> itemRequestsList = new ArrayList<>();
        for (ItemRequest request : requests) {
            itemRequestsList.add(itemsToRequest(request));
        }
        return itemRequestsList;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        //checkUserExistAndGet(userId);
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Page<ItemRequest> itemRequests = itemRequestRepository.findByIdIsNotOrderByCreatedAsc(userId, pageRequest);

        List<ItemRequestDto> result = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            result.add(itemsToRequest(itemRequest));
        }
        return result;

    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        checkUserExistAndGet(userId);
        checkRequest(requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).get();
        return itemsToRequest(itemRequest);

    }

    private User checkUserExistAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private void checkRequest(Long requestId) {
        if (!itemRequestRepository.existsById(requestId)) {
            throw new NotFoundException("Запроса не существует");
        }
    }

    private ItemRequestDto itemsToRequest(ItemRequest itemRequest) {

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, Collections.emptyList());
        List<Item> items = itemRepository.findByRequest_Id(itemRequest.getId());
        itemRequestDto.setItems(ItemMapper.returnItemDtoList(items));

        return itemRequestDto;
    }


}
