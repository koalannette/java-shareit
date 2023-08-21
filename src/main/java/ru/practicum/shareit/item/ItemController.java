package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.interfaces.Create;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader(REQUEST_HEADER) Long id, @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Поступил запрос на добавление вещи.");
        return itemService.createItem(id, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@RequestHeader(REQUEST_HEADER) Long ownerId,
                                      @RequestBody ItemDto itemDto, @PathVariable Long id) {
        log.info("Поступил запрос на обновление вещи.");
        return itemService.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse getItemById(@RequestHeader(REQUEST_HEADER) Long ownerId, @PathVariable Long id) {
        log.info("Получен запрос на получение вещи с id = {} .", id);
        return itemService.getItemById(ownerId, id);
    }

    @GetMapping
    public List<ItemDtoResponse> getItemsByUserId(@RequestHeader(REQUEST_HEADER) Long id) {
        log.info("Получен запрос на получение вещи с id пользователя = {} .", id);
        return itemService.getItemsByUserId(id);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByText(@RequestParam String text) {
        log.info("Получен запрос на получение вещи по тексту.");
        return itemService.getItemsByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse addComment(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @PathVariable Long itemId, @RequestBody @Valid CommentDto commentDto) {
        log.info("Получен запрос на добавление отзыва " +
                "для вещи с id = {} пользователем с id = {} .", itemId, userId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
