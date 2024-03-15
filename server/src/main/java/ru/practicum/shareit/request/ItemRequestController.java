package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestOutDto> addRequest(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                        @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequest request = requestService.addRequest(ItemRequestMapper.toItemRequest(itemRequestDto), userId);
        log.info("Пользователь с id {} добавил запрос с id: {}", userId, request.getId());
        return ResponseEntity.ok(ItemRequestMapper.toItemRequestOutDto(request));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestOutDto>> getUserRequests(@RequestHeader(X_SHARED_USER_ID) Long userId) {
        return ResponseEntity.ok(requestService.getUserRequests(userId).stream()
                .map(requestService::addItems)
                .collect(Collectors.toList()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestOutDto>> getAllRequests(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(requestService.getAllRequests(userId, from, size).stream()
                .map(requestService::addItems)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestOutDto> getRequest(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                        @PathVariable("requestId") Long requestId) {
        ItemRequest request = requestService.getRequest(userId, requestId);
        log.info("Пользователь с id {} просматривает запрос с id: {}", userId, requestId);
        return ResponseEntity.ok(requestService.addItems(request));
    }

}
