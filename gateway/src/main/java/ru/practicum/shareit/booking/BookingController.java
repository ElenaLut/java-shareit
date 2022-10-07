package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.IncorrectRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";
    private static final String DEFAULT_VALUE_FROM = "0";
    private static final String DEFAULT_VALUE_SIZE = "10";
    private static final String DEFAULT_VALUE_STATE = "ALL";

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookItemRequestDto bookingDto,
                                                @RequestHeader(name = USER_IN_HEADER) Long userId) {
        log.info("Запрос на создание бронирования {}, пользователем с id {}", bookingDto, userId);
        return bookingClient.addBookingItem(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@PathVariable Long bookingId,
                                                      @RequestHeader(name = USER_IN_HEADER) Long userId,
                                                      @RequestParam Boolean approved) {
        log.info("Запрос на обновление статуса бронирования {}, пользователем с id {}", bookingId, userId);
        return bookingClient.updateBookingItem(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(USER_IN_HEADER) Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Запрос на получение бронирования {}, пользователем с id {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserId(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM)
                                                         @PositiveOrZero int fromLine,
                                                         @RequestParam(defaultValue = DEFAULT_VALUE_SIZE) @Positive int size,
                                                         @RequestHeader(USER_IN_HEADER) Long userId,
                                                         @RequestParam(defaultValue = DEFAULT_VALUE_STATE) String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IncorrectRequestException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Запрос на получение бронирований, пользователем с id {}", userId);
        return bookingClient.getAllUserBookings(userId, bookingState, fromLine, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByOwner(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM)
                                                 @PositiveOrZero int fromLine,
                                                 @RequestParam(defaultValue = DEFAULT_VALUE_SIZE) @Positive int size,
                                                 @RequestHeader(USER_IN_HEADER) Long userId,
                                                 @RequestParam(defaultValue = DEFAULT_VALUE_STATE) String state) {
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IncorrectRequestException("Unknown state: UNSUPPORTED_STATUS"));
        log.info("Запрос на получение бронирований, владельцем с id {}", userId);
        return bookingClient.getAllOwnerBookings(userId, bookingState, fromLine, size);
    }
}