package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

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
    private final ItemForBookingDto item;

    @NotNull
    @Positive
    private long itemId;

    @NotNull
    @Positive
    private BookerForBookingDto booker;

    @NotNull
    private Status status;
}