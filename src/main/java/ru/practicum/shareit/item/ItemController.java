package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final static String REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(REQUEST_HEADER) Long id, @RequestBody @Valid ItemDto itemDto) {
        log.info("Поступил запрос на добавление вещи.");
        return itemService.createItem(id, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(REQUEST_HEADER) Long ownerId,
                              @RequestBody ItemDto itemDto, @PathVariable Long id) {
        return itemService.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader(REQUEST_HEADER) @PathVariable Long id) {
        log.info("Получен запрос на получение вещи с id = {} .", id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(REQUEST_HEADER) Long id) {
        log.info("Получен запрос на получение вещи с id пользователя = {} .", id);
        return itemService.getItemsByUserId(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByText(@RequestParam String text) {
        log.info("Получен запрос на получение вещи по тексту.");
        return itemService.getItemsByText(text);
    }


}
