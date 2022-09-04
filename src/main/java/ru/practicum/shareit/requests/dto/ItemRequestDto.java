package ru.practicum.shareit.requests.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequestDto {
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