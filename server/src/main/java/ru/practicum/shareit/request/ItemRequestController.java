package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@Slf4j
@Validated
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestOutDto addRequest(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequest request = requestService.addRequest(ItemRequestMapper.toItemRequest(itemRequestDto), userId);
        log.info("Пользователь с id {} добавил запрос с id: {}", userId, request.getId());
        return ItemRequestMapper.toItemRequestOutDto(request);
    }

    @GetMapping
    public List<ItemRequestOutDto> getUserRequests(@RequestHeader(X_SHARED_USER_ID) Long userId) {
        return requestService.getUserRequests(userId).stream()
                .map(requestService::addItems)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAllRequests(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        return requestService.getAllRequests(userId, from, size).stream()
                .map(requestService::addItems)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getRequest(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                        @Positive @PathVariable("requestId") Long requestId) {
        ItemRequest request = requestService.getRequest(userId, requestId);
        log.info("Пользователь с id {} просматривает запрос с id: {}", userId, requestId);
        return requestService.addItems(request);
    }

}
