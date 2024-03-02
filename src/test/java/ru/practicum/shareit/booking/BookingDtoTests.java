package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingDtoTests {

    @Autowired
    private JacksonTester<BookingOutDto> json;

    @Autowired
    private JacksonTester<BookingShortDto> jsonShort;

    private final LocalDateTime start = LocalDateTime.of(2024, 2, 2, 2, 2);
    private final LocalDateTime end = LocalDateTime.of(2024, 2, 3, 3, 3);

    @Test
    void testBookingOutDto() throws Exception {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        ItemOutDto testItemDto = ItemOutDto.builder()
                .id(2L)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        BookingOutDto bookingOutDto = BookingOutDto.builder()
                .id(3L)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .booker(userDto)
                .item(testItemDto)
                .build();

        JsonContent<BookingOutDto> result = json.write(bookingOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(3);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("Alex");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo(
                "alexFirst@yandex.ru");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("itemName");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
    }

    @Test
    void testBookingShortDto() throws Exception {

        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(4L)
                .bookerId(1L)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingShortDto> result = jsonShort.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(4);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(1);
    }

}
