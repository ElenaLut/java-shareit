package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";

    private final User user1 = new User(1L, "name1", "user1@user.ru");
    private final User user2 = new User(2L, "name2", "user2@user.ru");
    private final Item item = new Item(1L, "name", "description", true, user1, 1L, null);
    private final Booking booking = new Booking(1L, LocalDateTime.of(2022, 12, 1, 2, 2),
            LocalDateTime.of(2022, 12, 5, 2, 2), item, user2, Status.WAITING);
    private final BookingDto bookingDto = new BookingDto(1L, LocalDateTime.of(2022, 12, 1, 2, 2),
            LocalDateTime.of(2022, 12, 5, 2, 2), item, 1L, user2, Status.WAITING);
    private final BookingDto bookingDtoNew = new BookingDto(1L, LocalDateTime.of(2022, 12, 1, 2, 2),
            LocalDateTime.of(2022, 12, 5, 2, 2), null, 1L, null, null);

    @BeforeEach
    void beforeEach(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void createBookingTest() throws Exception {
        Mockito.when(bookingService.createBooking(Mockito.any(Booking.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(booking);
        mockMvc.perform(post("/bookings")
                        .header(USER_IN_HEADER, 2L)
                        .content(mapper.writeValueAsString(bookingDtoNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$.booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void changeBookingStatusTest() throws Exception {
        Mockito.when(bookingService.changeBookingStatus(1L, 1L, true)).thenReturn(booking);
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").value(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item").value(booking.getItem()))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()));
    }

    @Test
    void changeBookingIncorrectStatusTest() throws Exception {
        mockMvc.perform(patch("/bookings/1?approved=NOT_STATE")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getBookingByIdTest() throws Exception {
        Mockito.when(bookingService.getBookingById(1L, 1L)).thenReturn(booking);
        mockMvc.perform(get("/bookings/1")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.start").value(booking.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.end").value(booking.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.item").value(booking.getItem()))
                .andExpect(jsonPath("$.booker").value(booking.getBooker()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()));
    }

    @Test
    void getBookingByWrongIdTest() throws Exception {
        Mockito.when(bookingService.getBookingById(1L, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/bookings/1")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBookingsByUserIdWaitingTest() throws Exception {
        Mockito.when(bookingService.getAllBookingsByUserId(1, 1, 2L, "WAITING"))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings?state=WAITING&from=1&size=1")
                        .header(USER_IN_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllBookingsByUserIdDefault() throws Exception {
        Mockito.when(bookingService.getAllBookingsByUserId(0, 10, 2L, "ALL"))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings")
                        .header(USER_IN_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllBookingsByOwnerIdCurrentTest() throws Exception {
        Mockito.when(bookingService.getAllBookingsByOwnerId(1, 1, 1L, "WAITING"))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings/owner?state=WAITING&from=1&size=1")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllBookingsByOwnerIdDefault() throws Exception {
        Mockito.when(bookingService.getAllBookingsByOwnerId(0, 10, 1L, "ALL"))
                .thenReturn(List.of(booking));
        mockMvc.perform(get("/bookings/owner")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].start").value(bookingDto.getStart().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].end").value(bookingDto.getEnd().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].item").value(bookingDto.getItem()))
                .andExpect(jsonPath("$[0].booker").value(bookingDto.getBooker()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

}
