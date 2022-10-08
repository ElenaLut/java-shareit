package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking, Long userId, Long itemId);

    Booking changeBookingStatus(Long userId, Long bookingId, Boolean approved);

    Booking getBookingById(Long userId, Long bookingId);

    List<Booking> getAllBookingsByUserId(int fromLine, int size, Long userId, String state);

    List<Booking> getAllBookingsByOwnerId(int fromLine, int size, Long userId, String state);
}