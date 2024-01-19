package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item create(Long ownerId, Item item);

    Item update(Item item, Long itemId, Long userId);

    Item getById(Long itemId);

    List<Item> getUserItems(Long userId);

    List<Item> searchItems(String text);

    void delete(Long ownerId, Long itemId);

}
