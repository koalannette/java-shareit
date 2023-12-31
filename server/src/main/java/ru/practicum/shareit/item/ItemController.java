package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader(REQUEST_HEADER) Long id, @RequestBody ItemDto itemDto) {
        log.info("SERVER: Поступил запрос на добавление вещи.");
        return itemService.createItem(id, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@RequestHeader(REQUEST_HEADER) Long ownerId,
                                      @RequestBody ItemDto itemDto, @PathVariable Long id) {
        log.info("SERVER: Поступил запрос на обновление вещи.");
        return itemService.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse getItemById(@RequestHeader(REQUEST_HEADER) Long ownerId, @PathVariable Long id) {
        log.info("SERVER: Получен запрос на получение вещи с id = {} .", id);
        return itemService.getItemById(ownerId, id);
    }

    @GetMapping
    public List<ItemDtoResponse> getItemsByUserId(@RequestHeader(REQUEST_HEADER) Long id,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("SERVER: Получен запрос на получение вещи с id пользователя = {} .", id);
        return itemService.getItemsByUserId(id, from, size);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByText(@RequestParam String text,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("SERVER: Получен запрос на получение вещи по тексту.");
        return itemService.getItemsByText(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        log.info("SERVER: Получен запрос на добавление отзыва " +
                "для вещи с id = {} пользователем с id = {} .", itemId, userId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
