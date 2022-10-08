package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private long itemId;
    private Booker booker;
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