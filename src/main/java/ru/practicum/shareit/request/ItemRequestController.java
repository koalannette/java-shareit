package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSmallDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String REQUEST_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(REQUEST_HEADER) Long userId,
                                            @RequestBody @Valid ItemRequestSmallDto itemRequest) {
        log.info("Поступил запрос на добавление нового запроса вещи = {} .", itemRequest);
        return itemRequestService.createItemRequest(userId, itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByOwner(@RequestHeader(REQUEST_HEADER) Long ownerId) {
        log.info("Получен запрос на получение списка своих запросов вместе с данными об ответах на них");
        return itemRequestService.getRequestsByOwner(ownerId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(REQUEST_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) @Positive Integer from,
                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        if ((from == 0 && size == 0) || (size <= 0) || (from < 0)) {
            throw new NotAvailableException("Неверно переданы параметры from или size");
        }

        log.info("Получен запрос на получение списка запросов, созданных другими пользователями");
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(REQUEST_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        log.info("Получен запрос на получение данных об одном конкретном запросе");
        return itemRequestService.getRequestById(userId, requestId);
    }

}
