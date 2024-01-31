package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item create(Long ownerId, Item item) {
        checkUser(ownerId);
        return itemRepository.create(ownerId, item);
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        checkUser(userId);
        checkItem(itemId);
        return itemRepository.update(item, itemId, userId);
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.getById(itemId);
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        checkUser(userId);
        return itemRepository.getUserItems(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.searchItems(text);
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        checkUser(ownerId);
        checkItem(itemId);
        itemRepository.delete(ownerId, itemId);
    }

    private void checkUser(Long id) {
        userRepository.getById(id);
    }

    private void checkItem(Long id) {
        itemRepository.getById(id);
    }

}
