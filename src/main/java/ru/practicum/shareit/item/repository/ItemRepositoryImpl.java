package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long itemCounter = 0;

    @Override
    public Item create(Long ownerId, Item item) {
        generateItemId(item);
        item.setOwnerId(ownerId);
        items.put(item.getId(), item);
        Item newItem = items.get(item.getId());
        log.info("Добавлен предмет: {}", newItem);
        return newItem;
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        return null;
    }

    @Override
    public Item getById(Long itemId) {
        if(!items.containsKey(itemId)) {
            throw new DataNotFoundException(String.format("Не найден предмет c id: %s", itemId));
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return null;
    }

    @Override
    public List<Item> searchItems(String text) {
        return null;
    }

    @Override
    public void delete(Long ownerId, Long itemId) {

    }

    private void generateItemId(Item item) {
        item.setId(++itemCounter);
    }

}
