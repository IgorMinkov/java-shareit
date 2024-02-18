package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {

    @Positive
    private Long id;

    @NotBlank
    private String description;

    @NotEmpty
    private List<ItemDto> items;

    @NotNull
    @PastOrPresent
    private LocalDateTime created;

}
