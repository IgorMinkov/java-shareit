package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
public class ItemDto {

    @Positive(message = "id must be positive")
    private Long id;

    @NotBlank(message = "Name cannot be empty or contain spaces.")
    private String name;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Available cannot be null")
    private Boolean available;

    @Positive(message = "request_id must be positive")
    private Long requestId;

}
