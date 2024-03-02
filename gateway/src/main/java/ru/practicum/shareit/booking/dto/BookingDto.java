package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDto {

    @Positive(message = "id must be positive")
    private Long itemId;

    @NotNull(message = "start cannot be null")
    @FutureOrPresent(message = "start may be in the present or future")
    private LocalDateTime start;

    @NotNull(message = "end cannot be null")
    @Future(message = "end may be in the future")
    private LocalDateTime end;

}
