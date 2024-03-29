package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingOutDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;

    private UserDto booker;

    private ItemOutDto item;

}
