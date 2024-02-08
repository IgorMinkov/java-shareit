package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static ItemOutDto toItemOutDto(Item item) {
        return new ItemOutDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable());
    }

    public static Item toItem(ItemOutDto itemOutDto, Long ownerId) {
        return new Item(
                itemOutDto.getId(),
                itemOutDto.getName(),
                itemOutDto.getDescription(),
                itemOutDto.getAvailable(),
                ownerId);
    }

    public static Item toItem(ItemDto itemDto, Long ownerId) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId);
    }

}
