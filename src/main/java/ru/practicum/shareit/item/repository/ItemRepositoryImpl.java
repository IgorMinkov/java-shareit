package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    @Override
    public Item create(Long ownerId, Item item) {
        return null;
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        return null;
    }

    @Override
    public Item getById(Long itemId) {
        return null;
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
}
