package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item create(Long ownerId, Item item) {
        checkUser(ownerId);
        itemRepository.save(item);
        Item newItem = getById(item.getId());
        log.info("Добавлен предмет: {}", newItem);
        return newItem;
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        checkUser(userId);
        checkItem(itemId);
        Item newItem = getById(itemId);
        if (!Objects.equals(newItem.getOwnerId(), userId)) {
            throw new DataNotFoundException(
                    String.format("Пользователь %s не владелец предмета c id: %s", userId, itemId)
            );
        }
        Optional.ofNullable(item.getName()).ifPresent(newItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(newItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(newItem::setAvailable);
        itemRepository.save(newItem);
        log.info("Обновлен предмет: {}", newItem);
        return newItem;
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Не найден предмет c id: %s", itemId)));
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        checkUser(userId);
        return itemRepository.findByOwnerId(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        checkUser(ownerId);
        checkItem(itemId);
        Item item = getById(itemId);
        if (!Objects.equals(item.getOwnerId(), ownerId)) {
            throw new DataNotFoundException(
                    String.format("У пользователя %s не найден предмет c id: %s", ownerId, itemId)
            );
        }
        itemRepository.deleteById(itemId);
        log.info("Удален предмет с id: {} у пользователя с id: {}", itemId, ownerId);
    }

    private void checkUser(Long id) {
        userService.checkUser(id);
    }

    private void checkItem(Long id) {
        if(itemRepository.existsById(id)) {
            throw new DataNotFoundException(String.format("Не найден предмет c id: %s", id));
        }
    }

}
