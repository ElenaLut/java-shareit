package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";
    private static final String DEFAULT_VALUE_FROM = "0";
    private static final String DEFAULT_VALUE_SIZE = "10";
    private static final String DEFAULT_VALUE_STATE = "ALL";

    private final BookingService service;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto,
                                    @RequestHeader(name = USER_IN_HEADER) Long userId) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        return BookingMapper.toBookingDto(service.createBooking(booking, userId, bookingDto.getItemId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@PathVariable Long bookingId,
                                          @RequestHeader(name = USER_IN_HEADER) Long userId,
                                          @RequestParam Boolean approved) {
        return BookingMapper.toBookingDto(service.changeBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_IN_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(service.getBookingById(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM)
                                                   @PositiveOrZero int fromLine,
                                                   @RequestParam(defaultValue = DEFAULT_VALUE_SIZE) @Positive int size,
                                                   @RequestHeader(USER_IN_HEADER) Long userId,
                                                   @RequestParam(defaultValue = DEFAULT_VALUE_STATE) String state) {
        return BookingMapper.toBookingDtoList(service.getAllBookingsByUserId(fromLine, size, userId, state));
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM)
                                           @PositiveOrZero int fromLine,
                                           @RequestParam(defaultValue = DEFAULT_VALUE_SIZE) @Positive int size,
                                           @RequestHeader(USER_IN_HEADER) Long userId,
                                           @RequestParam(defaultValue = DEFAULT_VALUE_STATE) String state) {
        return BookingMapper.toBookingDtoList(service.getAllBookingsByOwnerId(fromLine, size, userId, state));
    }
}