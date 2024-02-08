package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOutDto;

@Data
@AllArgsConstructor
public class ItemOutDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingOutDto lastBooking;

    private BookingOutDto nextBooking;

    public ItemOutDto(Long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

}