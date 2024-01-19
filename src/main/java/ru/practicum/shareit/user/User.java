package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;

@Data
@EqualsAndHashCode(of = {"id", "email"})
@AllArgsConstructor
public class User {

    @Positive
    private Long id;

    @NotBlank
    private String name;

    @NotEmpty
    @Email
    private String email;

}
