package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

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

}
