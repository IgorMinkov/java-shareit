package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Long ownerId, Item item, Long requestId);

    Item update(Item item, Long itemId, Long userId);

    Item getById(Long itemId);

    List<Item> getOwnerItems(Long userId, Integer from, Integer size);

    List<Item> searchItems(String text, Integer from, Integer size);

    void delete(Long ownerId, Long itemId);

    Comment addComment(Long userId, Comment comment, Long itemId);

    void checkItem(Long itemId);

    ItemOutDto addBookingAndComments(Item item, Long userId);

    List<Item> getByRequestId(Long requestId);

}
