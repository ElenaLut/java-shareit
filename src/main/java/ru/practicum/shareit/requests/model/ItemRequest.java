package ru.practicum.shareit.requests.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
public class ItemRequest {

    private long id;
    private String description;
    private long requester;
    private LocalDateTime created;
}