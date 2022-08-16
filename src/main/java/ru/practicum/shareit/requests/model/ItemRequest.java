package ru.practicum.shareit.requests.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class ItemRequest {
    @NotNull
    @Positive
    private long id;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    @Positive
    private long requester;

    @NotNull
    private LocalDateTime created;
}