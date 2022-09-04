package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_IN_HEADER = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto bookingDto,
                                    @RequestHeader(name = USER_ID_IN_HEADER) Long userId) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        return BookingMapper.toBookingDto(service.createBooking(booking, userId, bookingDto.getItemId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeBookingStatus(@PathVariable Long bookingId,
                                          @RequestHeader(name = USER_ID_IN_HEADER) Long userId,
                                          @RequestParam Boolean approved) {
        return BookingMapper.toBookingDto(service.changeBookingStatus(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                     @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(service.getBookingById(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByUserId(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllBookingsByUserId(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader(USER_ID_IN_HEADER) Long userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllBookingsByOwnerId(userId, state)
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}