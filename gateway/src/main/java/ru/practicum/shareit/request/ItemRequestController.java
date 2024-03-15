package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@Slf4j
@Validated
@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Пользователь с id {} добавил запрос бронирования", userId);
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(X_SHARED_USER_ID) Long userId) {
        log.info("Пользователь с id {} запрашивает свои бронирования", userId);
        return itemRequestClient.getRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Пользователь с id {} запрашивает список всех бронирований", userId);
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                        @Positive @PathVariable("requestId") Long requestId) {
        log.info("Пользователь с id {} запрашивает запрос бронирования с id {}", userId, requestId);
        log.info("Пользователь с id {} просматривает запрос с id: {}", userId, requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

}
