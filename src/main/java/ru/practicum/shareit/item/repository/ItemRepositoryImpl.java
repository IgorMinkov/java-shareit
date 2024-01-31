package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
        Item newItem = items.get(itemId);
        if (!Objects.equals(newItem.getOwnerId(), userId)) {
            throw new DataNotFoundException(
                    String.format("Пользователь %s не владелец предмета c id: %s", userId, itemId)
            );
        }
        Optional.ofNullable(item.getName()).ifPresent(newItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(newItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(newItem::setAvailable);
        items.put(itemId, newItem);
        log.info("Обновлен предмет: {}", newItem);
        return newItem;
    }

    @Override
    public Item getById(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new DataNotFoundException(String.format("Не найден предмет c id: %s", itemId));
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        List<Item> userItems = items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .collect(Collectors.toList());
        if (userItems.isEmpty()) {
            return new ArrayList<>();
        }
        return userItems;
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> nameResult = items.values().stream()
                .filter(item -> StringUtils.containsIgnoreCase(item.getName(), text))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        Set<Item> result = new HashSet<>(nameResult);

        List<Item> descriptionResult = items.values().stream()
                .filter(item -> StringUtils.containsIgnoreCase(item.getDescription(), text))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        result.addAll(descriptionResult);

        if (nameResult.isEmpty() && descriptionResult.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        Item item = items.get(itemId);
        if (!Objects.equals(item.getOwnerId(), ownerId)) {
            throw new DataNotFoundException(
                    String.format("У пользователя %s не найден предмет c id: %s", ownerId, itemId)
            );
        }
        items.remove(itemId);
        log.info("Удален предмет с id: {} у пользователя с id: {}", itemId, ownerId);
    }

    private void generateItemId(Item item) {
        item.setId(++itemCounter);
    }

}
