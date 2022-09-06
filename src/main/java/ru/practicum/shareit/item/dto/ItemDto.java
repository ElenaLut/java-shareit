package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    @NotNull
    @Positive
    private long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private Boolean available;

    @JsonIgnore
    private UserForItemDto owner;
    private Long requestId;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public void setLastBooking(Optional<Booking> booking) {
        if (booking.isPresent()) {
            this.lastBooking = new BookingForItemDto(
                    booking.get().getId(),
                    booking.get().getBooker().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd()
            );
        }
    }

    public void setNextBooking(Optional<Booking> booking) {
        if (booking.isPresent()) {
            this.nextBooking = new BookingForItemDto(
                    booking.get().getId(),
                    booking.get().getBooker().getId(),
                    booking.get().getStart(),
                    booking.get().getEnd()
            );
        }
    }
}