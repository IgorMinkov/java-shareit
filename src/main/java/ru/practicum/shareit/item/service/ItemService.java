package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Long ownerId, Item item);

    Item update(Item item, Long itemId, Long userId);

    Item getById(Long itemId);

    List<Item> getUserItems(Long userId);

    List<Item> searchItems(String text);

}
