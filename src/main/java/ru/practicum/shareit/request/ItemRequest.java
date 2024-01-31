package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@AllArgsConstructor
public class ItemRequest {

    @Positive
    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private User requester;

    @NotNull
    @PastOrPresent
    private LocalDateTime created;

}
