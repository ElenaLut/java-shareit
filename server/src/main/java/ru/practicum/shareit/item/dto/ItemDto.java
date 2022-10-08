package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private Long requestId;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public void setLastBooking(Optional<Booking> booking) {
        if (booking.isPresent()) {
            this.lastBooking = new BookingShortDto(
                    booking.get().getId(),
                    booking.get().getBooker().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd()
            );
        }
    }

    public void setNextBooking(Optional<Booking> booking) {
        if (booking.isPresent()) {
            this.nextBooking = new BookingShortDto(
                    booking.get().getId(),
                    booking.get().getBooker().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd()
            );
        }
    }
}