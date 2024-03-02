package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    public static final String X_SHARED_USER_ID = "X-Sharer-User-Id";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto) {
        log.info("Пользователь {} добавляет предмет: {}", userId, itemDto.getId());
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @RequestBody ItemDto itemDto,
            @Positive @PathVariable Long itemId) {
        log.info("Владелец {} обновил предмет: {}", userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @Positive @PathVariable Long itemId) {
        log.info("Пользователь {} запросил предмет: {}", userId, itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllOwnerItems(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Пользователь {} запросил список своих вещей", userId);
        return itemClient.getItemsUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(
            @RequestParam(defaultValue = "") String text,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запущен поиск по тексту: {}", text);
        return itemClient.searchItem(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @Positive @PathVariable Long itemId) {
        log.info("Владелец {} удаляет предмет: {}", userId, itemId);
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @Valid @RequestBody CommentDto commentDto,
            @Positive @PathVariable Long itemId) {
        log.info("Пользователь с id: {} комментирует вещь с id: {}", userId, itemId);
        return itemClient.addComment(userId, itemId, commentDto);
    }

}
