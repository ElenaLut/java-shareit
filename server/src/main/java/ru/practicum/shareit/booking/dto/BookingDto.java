package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    @Positive
    private long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;
    private Item item;

    @Positive
    private long itemId;

    @NotNull
    @Positive
    private Booker booker;

    @NotNull
    private Status status;

    @Data
    @Builder
    public static class Booker {
        private final long id;
        private final String name;
    }

    @Data
    @Builder
    public static class Item {
        private final long id;
        private final String name;
    }
}