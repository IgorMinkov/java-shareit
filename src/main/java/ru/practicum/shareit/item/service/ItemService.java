package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Long ownerId, Item item);

    Item update(Item item, Long itemId, Long userId);

    Item getById(Long itemId, Long userId);

    List<Item> getOwnerItems(Long userId);

    List<Item> searchItems(String text);

    void delete(Long ownerId, Long itemId);

    CommentOutDto addComment(Long userId, Comment comment, Long itemId);

    void checkItem(Long itemId);

    ItemOutDto addBookingAndCommentsToItem(ItemOutDto dto);

}
