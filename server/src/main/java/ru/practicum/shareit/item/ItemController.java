package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    public static final String X_SHARED_USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @RequestBody ItemDto itemDto) {
        Item item = itemService.create(userId, ItemMapper.toItem(itemDto), itemDto.getRequestId());
        log.info("Пользователь {} добавил предмет: {}", userId, item.getName());
        return ResponseEntity.ok(ItemMapper.toItemDto(item));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @RequestBody ItemDto itemDto,
            @PathVariable Long itemId) {
        Item updateItem = itemService.update(ItemMapper.toItem(itemDto), itemId, userId);
        log.info("Владелец {} обновил предмет: {}", userId, updateItem.getName());
        return ResponseEntity.ok(ItemMapper.toItemDto(updateItem));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemOutDto> getItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @PathVariable Long itemId) {
        Item item = itemService.getById(itemId);
        return ResponseEntity.ok(itemService.addBookingAndComments(item, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemOutDto>> getAllOwnerItems(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(itemService.getOwnerItems(userId, from, size).stream()
                .map(item -> itemService.addBookingAndComments(item, userId))
                .collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запущен поиск по тексту: {}", text);
        return ResponseEntity.ok(itemService.searchItems(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
        log.info("Предмет с id: {} удален владельцем", itemId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentOutDto> addComment(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @RequestBody CommentDto commentDto,
            @PathVariable Long itemId) {
        log.info("Пользователь с id: {} комментирует вещь с id: {}", userId, itemId);
        Comment comment = itemService.addComment(userId, CommentMapper.toComment(commentDto), itemId);
        return ResponseEntity.ok(CommentMapper.toCommentOutDto(comment));
    }

}
