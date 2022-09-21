package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .itemId(2L)
            .start(LocalDateTime.of(2023, 8, 19, 15, 0, 0))
            .end(LocalDateTime.of(2023, 9, 19, 15, 0, 0))
            .build();
    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(LocalDateTime.of(2023, 8, 19, 15, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(LocalDateTime.of(2023, 9, 19, 15, 0, 0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}