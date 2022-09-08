package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @NotNull
    @Positive
    private long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
    private final Item item;

    @NotNull
    @Positive
    private long itemId;

    @NotNull
    @Positive
    private User booker;

    @NotNull
    private Status status;
}