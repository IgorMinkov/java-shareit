package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    public static final String X_SHARED_USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        Item item = itemService.create(userId,ItemMapper.toItem(itemDto, userId));
        log.info("Польователь {} добавил предмет: {}", userId, item.getName());
        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @RequestBody ItemDto itemDto,
            @Positive @PathVariable Long itemId
    ) {
        Item updateItem = itemService.update(ItemMapper.toItem(itemDto, userId), itemId, userId);
        log.info("Владелец {} обновил предмет: {}", userId, updateItem.getName());
        return ItemMapper.toItemDto(updateItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@Positive @PathVariable Long itemId) {
        Item item = itemService.getById(itemId);
        log.info("Найден предмет по id: {} ", itemId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping
    public List<ItemOutDto> getAllUserItems(@RequestHeader(X_SHARED_USER_ID) Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(defaultValue = "") String text) {
        log.info("Запущен поиск по тексту: {}", text);
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @Positive @PathVariable Long itemId
    ) {
        itemService.delete(userId, itemId);
        log.info("Предмет с id: {} удален владельцем", itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutDto addComment(
            @RequestHeader(X_SHARED_USER_ID) Long userId,
            @Valid @RequestBody CommentDto commentDto,
            @Positive @PathVariable Long itemId
    ) {
        log.info("Пользователь с id: {} комментирует ведь с id: {}", userId, itemId);
        return itemService.addComment(userId, CommentMapper.toComment(userId, commentDto, itemId), itemId);
    }

}
