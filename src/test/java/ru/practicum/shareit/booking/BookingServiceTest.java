package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final User user1 = new User(1L, "name1", "user1@user.ru");
    private final User user2 = new User(2L, "name2", "user2@user.ru");
    private final Item item1 = new Item(1L, "name", "description", true, user2, 1L, null);
    private final Booking booking = new Booking(1L, LocalDateTime.of(2022, 11, 1, 9, 0),
            LocalDateTime.of(2022, 11, 3, 15, 0), item1, user1, Status.WAITING);
    private final Booking bookingNew = new Booking(1L, LocalDateTime.of(2022, 11, 1, 9, 0),
            LocalDateTime.of(2022, 11, 3, 15, 0), null, null, null);
    private final Booking bookingApproved = new Booking(1L, LocalDateTime.of(2022, 11, 1, 9, 0),
            LocalDateTime.of(2022, 11, 3, 15, 0), item1, user1, Status.APPROVED);

    @BeforeEach
    void beforeEach() {
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService);
    }

    @Test
    void createBookingTest() {
        Mockito.when(itemService.getItemById(1L)).thenReturn(item1);
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);
        Booking result = bookingService.createBooking(bookingNew, 1L, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking, result);
    }

    @Test
    void createBookingNotAvailableItemTest() {
        Item itemNotAvailable = new Item(3L, "name3", "description3", false, user2, 1L, null);
        Mockito.when(itemService.getItemById(3L)).thenReturn(itemNotAvailable);
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);
        Assertions.assertThrows(IncorrectRequestException.class,
                () -> bookingService.createBooking(bookingNew, 1L, 3L));
    }

    @Test
    void changeBookingStatusApprovedTest() {
        Mockito.when(userService.getUserById(2L)).thenReturn(user2);
        Mockito.when(itemService.getItemById(1L)).thenReturn(item1);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(bookingApproved);
        Booking result = bookingService.changeBookingStatus(2L, 1L, true);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookingApproved, result);
    }

    @Test
    void changeBookingStatusAlreadyApprovedTest() {
        Mockito.when(userService.getUserById(2L)).thenReturn(user2);
        Mockito.when(itemService.getItemById(1L)).thenReturn(item1);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(bookingApproved));
        Assertions.assertThrows(IncorrectRequestException.class,
                () -> bookingService.changeBookingStatus(2L, 1L, true));
    }

    @Test
    void changeBookingStatusByIncorrectOwnerIdTest() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user1);
        Mockito.when(itemService.getItemById(1L)).thenReturn(item1);
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Assertions.assertThrows(BookingException.class,
                () -> bookingService.changeBookingStatus(1L, 1L, false));
    }

    @Test
    void getBookingByIdTest() {
        Mockito.when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        Booking result = bookingService.getBookingById(1L, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking, result);
    }

    @Test
    void getBookingByWrongIdTest() {
        Mockito.when(bookingRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.getBookingById(1L, 10L));
    }

    @Test
    void getAllBookingsByUserIdCurrentTest() {
        Mockito.when(bookingRepository.getBookingsByBookerIdInTime(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByUserId(0, 10, 1L, "CURRENT"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByUserIdFutureTest() {
        Mockito.when(bookingRepository.getBookingsByBookerIdAfter(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByUserId(0, 10, 1L, "FUTURE"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByUserIdPastTest() {
        Mockito.when(bookingRepository.getBookingsByBookerIdBefore(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByUserId(0, 10, 1L, "PAST"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByUserIdAllTest() {
        Mockito.when(bookingRepository.getBookingsByBookerId(Mockito.anyLong(),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByUserId(0, 10, 1L, "ALL"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByUserIdRejectedTest() {
        Mockito.when(bookingRepository.getBookingsByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(Status.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByUserId(0, 10, 1L, "REJECTED"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByUserIdWaitingTest() {
        Mockito.when(bookingRepository.getBookingsByBookerIdAndStatus(Mockito.anyLong(), Mockito.any(Status.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByUserId(0, 10, 1L, "WAITING"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByUserIdIncorrectStateTest() {
        Assertions.assertThrows(IncorrectRequestException.class,
                () -> bookingService.getAllBookingsByUserId(0, 10, 1L, "NOT_STATE"));
    }

    @Test
    void getAllBookingsByOwnerIdCurrentTest() {
        Mockito.when(bookingRepository.getBookingsByOwnerIdInTime(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByOwnerId(0, 10, 1L, "CURRENT"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByOwnerIdFutureTest() {
        Mockito.when(bookingRepository.getBookingsByOwnerIdAfter(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByOwnerId(0, 10, 1L, "FUTURE"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByOwnerIdPastTest() {
        Mockito.when(bookingRepository.getBookingsByOwnerIdBefore(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByOwnerId(0, 10, 1L, "PAST"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByOwnerIdAllTest() {
        Mockito.when(bookingRepository.getBookingsByOwner(Mockito.anyLong(),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByOwnerId(0, 10, 1L, "ALL"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByOwnerIdRejectedTest() {
        Mockito.when(bookingRepository.getBookingsByOwnerIdAndStatus(Mockito.anyLong(), Mockito.any(Status.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByOwnerId(0, 10, 1L, "REJECTED"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByOwnerIdWaitingTest() {
        Mockito.when(bookingRepository.getBookingsByOwnerIdAndStatus(Mockito.anyLong(), Mockito.any(Status.class),
                Mockito.any(Pageable.class))).thenReturn(List.of(booking));
        List<Booking> bookingsList = new ArrayList<>(bookingService.getAllBookingsByOwnerId(0, 10, 1L, "WAITING"));
        Assertions.assertNotNull(bookingsList);
        Assertions.assertEquals(booking, bookingsList.get(0));
    }

    @Test
    void getAllBookingsByOwnerIdIncorrectStateTest() {
        Assertions.assertThrows(IncorrectRequestException.class,
                () -> bookingService.getAllBookingsByOwnerId(0, 10, 1L, "NOT_STATE"));
    }
}