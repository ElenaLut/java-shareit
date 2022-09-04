package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.booker.id = ?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByBookerId(Long userId);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.booker.id = ?1 " +
            "AND      b.end < ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByBookerIdBefore(Long userId, LocalDateTime now);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.booker.id = ?1 " +
            "AND      b.start > ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByBookerIdAfter(Long userId, LocalDateTime now);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.booker.id = ?1 " +
            "AND      ?2 BETWEEN b.start AND  b.end  " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByBookerIdInTime(Long userId, LocalDateTime now);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.booker.id = ?1 " +
            "AND      b.status = ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByBookerIdAndStatus(Long userId, Status bookingStatus);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.item.owner.id = ?1 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByOwner(Long userId);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.item.owner.id = ?1 " +
            "AND      b.end < ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByOwnerIdBefore(Long userId, LocalDateTime now);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.item.owner.id = ?1 " +
            "AND      b.start > ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByOwnerIdAfter(Long userId, LocalDateTime now);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.item.owner.id = ?1 " +
            "AND      ?2 BETWEEN b.start AND b.end " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByOwnerIdInTime(Long userId, LocalDateTime now);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.item.owner.id = ?1 " +
            "AND      b.status = ?2 " +
            "ORDER BY b.start DESC ")
    List<Booking> getBookingsByOwnerIdAndStatus(Long userId, Status bookingStatus);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.booker.id = ?1 " +
            "AND      b.item.id = ?2 " +
            "AND      b.end < ?3 " +
            "AND      b.status = 'APPROVED'"
    )
    Booking getCompletedBooking(Long userId, Long itemId, LocalDateTime now);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.item.id = ?1 " +
            "AND      b.end < ?2 " +
            "ORDER BY b.end DESC ")
    List<Booking> getLastBookingByItemId(Long itemId, LocalDateTime end);

    @Query("SELECT    b FROM Booking AS b " +
            "WHERE    b.item.id = ?1 " +
            "AND      b.start > ?2 " +
            "ORDER BY b.start ASC ")
    List<Booking> getNextBookingByItemId(Long itemId, LocalDateTime start);
}