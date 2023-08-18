package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingBookerDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDtoResponse createItem(Long ownerId, ItemDto itemDto) {
        User user = checkUserExistAndGet(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        return ItemMapper.toItemDtoResponseFromItem(itemRepository.save(item));
    }

    @Override
    public ItemDtoResponse updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        Item updateItem = checkItemExistAndGet(itemId);

        if (!Objects.equals(updateItem.getOwner().getId(), ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не является владельцем");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            updateItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            updateItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updateItem.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDtoResponseFromItem(itemRepository.save(updateItem));
    }

    @Override
    public ItemDtoResponse getItemById(Long userId, Long itemId) {
        Item item = checkItemExistAndGet(itemId);
        ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponseFromItem(item);
        itemDtoResponse.setComments(new HashSet<>(getItemComments(itemId)));
        if (item.getOwner().getId().equals(userId)) {
            Booking lastBooking = bookingRepository
                    .findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(itemId, LocalDateTime.now(), Status.APPROVED);
            Booking nextBooking = bookingRepository
                    .findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(itemId, LocalDateTime.now(), Status.APPROVED);
            itemDtoResponse.setLastBooking(BookingMapper.toBookingBookerDto(lastBooking));
            itemDtoResponse.setNextBooking(BookingMapper.toBookingBookerDto(nextBooking));
        }
        return itemDtoResponse;
    }

    @Override
    public List<ItemDtoResponse> getItemsByUserId(Long id) {
        checkUserExistAndGet(id);
        List<Item> items = itemRepository.findItemByOwner_Id(id);

        List<ItemDtoResponse> itemDtoResponses = items.stream()
                .map(item -> ItemDtoResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .lastBooking(getLastBookingForItem(item.getId()))
                        .nextBooking(getNextBookingForItem(item.getId()))
                        .available(item.getAvailable())
                        .build())
                .sorted(Comparator.comparing(ItemDtoResponse::getId))
                .collect(Collectors.toList());
        log.info("Список вещей пользователя c id = {}: {}", id, itemDtoResponses);
        return itemDtoResponses;
    }


    @Override
    public List<ItemDtoResponse> getItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findItemByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text);
        return ItemMapper.toItemDtoResponseListFromItemList(items);
    }

    @Override
    public CommentDtoResponse addComment(CommentDto dto, Long itemId, Long userId) {
        if (!bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(itemId, userId,
                Status.APPROVED, LocalDateTime.now())) {
            throw new NotAvailableException("Пользователь  " + userId + " не арендовал вещь " + itemId);
        } else {
            User author = userRepository.findById(userId).orElseThrow(() ->
                    new NotFoundException("Пользователь " + userId + " не найден"));
            Item item = itemRepository.findById(itemId).orElseThrow(() ->
                    new NotFoundException("Вещь " + itemId + " не найдена"));
            Comment comment = CommentMapper.toComment(dto, author, item);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toCommentDtoResponseFromComment(commentRepository.save(comment));
        }
    }

    private User checkUserExistAndGet(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private Item checkItemExistAndGet(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с id " + itemId + " не найдена."));
    }


    private BookingBookerDto getLastBookingForItem(Long itemId) {
        return BookingMapper.toBookingBookerDto(
                bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(
                        itemId, LocalDateTime.now(), Status.APPROVED));
    }

    private BookingBookerDto getNextBookingForItem(Long itemId) {
        return BookingMapper.toBookingBookerDto(
                bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(
                        itemId, LocalDateTime.now(), Status.APPROVED));
    }

    private List<CommentDtoResponse> getItemComments(Long itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDtoResponseFromComment).collect(Collectors.toList());
    }
}
