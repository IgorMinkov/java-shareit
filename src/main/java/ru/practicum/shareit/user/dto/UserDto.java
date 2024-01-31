package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class UserDto {

    @Positive
    private Long id;

    @NotBlank
    private String name;

    @NotEmpty
    @Email
    private String email;

}
