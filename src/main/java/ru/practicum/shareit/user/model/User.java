package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class User {
    @NotNull
    private long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;
}