package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, ItemService itemService, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    public Booking createBooking(Booking booking, Long userId, Long itemId) {
        User user = userService.getUserById(userId);
        if (itemId <= 0) {
            throw new IncorrectRequestException("Идентификатор вещи не может быть меньше или равен нулю. Id вещи " + itemId);
        }
        Item item = itemService.getItemById(itemId);
        if (item.getOwner().getId() == userId) {
            log.error("Владелец вещи не может забронировать ее");
            throw new BookingException("Ошибка бронирования: владелец не модет забронировать свою вещь");
        }
        if (!item.getAvailable()) {
            log.error("Вещь с id {} недоступна", itemId);
            throw new IncorrectRequestException("Вещь недоступна");
        }
        if (booking.getStart().isBefore(LocalDateTime.now()) || booking.getEnd().isBefore(booking.getStart())
                || booking.getEnd().isBefore(LocalDateTime.now())) {
            log.error("В указанный период вещь недоступна");
            throw new IncorrectRequestException("Некорректные даты бронирования");
        }
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking changeBookingStatus(Long userId, Long bookingId, Boolean approved) {
        userService.getUserById(userId);
        Booking booking = getBookingById(userId, bookingId);
        Item item = itemService.getItemById(booking.getItem().getId());
        if (userId != item.getOwner().getId()) {
            log.error("Пользователь с id {} не является владельцем вещи с id {}", userId, item.getId());
            throw new BookingException("Только владелец вещи может изменять статус бронирвоания");
        }
        if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
            log.error("Бронирование с id {} уже обработано", bookingId);
            throw new IncorrectRequestException("Статус бронирвоание уже был изменен");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            log.info("Бронирование с id {} подтверждено", bookingId);
        } else {
            booking.setStatus(Status.REJECTED);
            log.info("Бронирвоание с id {} отклонено", bookingId);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long bookingId) {
        userService.checkIfUserExists(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new NotFoundException("Не найдено бронирование с id " + bookingId));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            log.error("Информация о бронированировании не доступна пользователю с id {} ", userId);
            throw new BookingException("Информация о бронировании доступна только автору бронирвоания или владельцу вещи");
        }
        log.info("Бронирование с id {} получено", bookingId);
        return booking;
    }

    @Override
    public List<Booking> getAllBookingsByUserId(Long userId, String state) {
        userService.checkIfUserExists(userId);
        List<Booking> bookingsOfUser = new ArrayList<>();
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (bookingState.equals(State.CURRENT)) {
            bookingsOfUser = bookingRepository.getBookingsByBookerIdInTime(userId, LocalDateTime.now());
            log.info("Пользователю с id {} передан список его текущих бронирований", userId);
        } else if (bookingState.equals(State.FUTURE)) {
            bookingsOfUser = bookingRepository.getBookingsByBookerIdAfter(userId, LocalDateTime.now());
            log.info("Пользователю с id {} передан список его будущих бронирований", userId);
        } else if (bookingState.equals(State.PAST)) {
            bookingsOfUser = bookingRepository.getBookingsByBookerIdBefore(userId, LocalDateTime.now());
            log.info("Пользователю с id {} передан список его прошлых бронирований", userId);
        } else if (bookingState.equals(State.ALL)) {
            bookingsOfUser = bookingRepository.getBookingsByBookerId(userId);
            log.info("Пользователю с id {} передан список всех его бронирований", userId);
        } else if (bookingState.equals(State.REJECTED)) {
            bookingsOfUser = bookingRepository.getBookingsByBookerIdAndStatus(userId, Status.REJECTED);
            log.info("Пользователю с id {} передан список бронирований со статусом - отклонен", userId);
        } else if (bookingState.equals(State.WAITING)) {
            bookingsOfUser = bookingRepository.getBookingsByBookerIdAndStatus(userId, Status.WAITING);
            log.info("Пользователю с id {} передан список бронирований со статусом - в ожидании", userId);
        }
        return bookingsOfUser;
    }

    @Override
    public List<Booking> getAllBookingsByOwnerId(Long userId, String state) {
        userService.checkIfUserExists(userId);
        List<Booking> bookingsOfOwner = new ArrayList<>();
        State bookingState;
        try {
            bookingState = State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        if (bookingState.equals(State.CURRENT)) {
            bookingsOfOwner = bookingRepository.getBookingsByOwnerIdInTime(userId, LocalDateTime.now());
            log.info("Пользователю с id {} передан список текущих бронирований его вещей", userId);
        } else if (bookingState.equals(State.FUTURE)) {
            bookingsOfOwner = bookingRepository.getBookingsByOwnerIdAfter(userId, LocalDateTime.now());
            log.info("Пользователю с id {} передан список будущих бронирований его вещей", userId);
        } else if (bookingState.equals(State.PAST)) {
            bookingsOfOwner = bookingRepository.getBookingsByOwnerIdBefore(userId, LocalDateTime.now());
            log.info("Пользователю с id {} передан список прошлых бронирований его вещей", userId);
        } else if (bookingState.equals(State.ALL)) {
            bookingsOfOwner = bookingRepository.getBookingsByOwner(userId);
            log.info("Пользователю с id {} передан список всех бронирований его вещей", userId);
        } else if (bookingState.equals(State.REJECTED)) {
            bookingsOfOwner = bookingRepository.getBookingsByOwnerIdAndStatus(userId, Status.REJECTED);
            log.info("Пользователю с id {} передан список бронирований со статусом - отклонен", userId);
        } else if (bookingState.equals(State.WAITING)) {
            bookingsOfOwner = bookingRepository.getBookingsByOwnerIdAndStatus(userId, Status.WAITING);
            log.info("Пользователю с id {} передан список бронирований со статусом - в ожидании", userId);
        }
        return bookingsOfOwner;
    }
}