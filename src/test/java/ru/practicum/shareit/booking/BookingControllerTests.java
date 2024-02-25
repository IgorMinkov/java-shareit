package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private final LocalDateTime start = LocalDateTime.of(2024, 3, 2, 2, 2);
    private final LocalDateTime end = LocalDateTime.of(2024, 3, 3, 3, 3);

    private BookingDto bookingDto;
    private BookingOutDto bookingOutDto;
    private Booking booking;
    private Long bookingId;

    @BeforeEach
    void setUp() {
        bookingId = 1L;

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();

        User user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        Item item = Item.builder()
                .id(2L)
                .name("itemName")
                .description("item description")
                .available(true)
                .owner(user)
                .build();

        ItemOutDto itemOutDto = ItemOutDto.builder()
                .id(2L)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        booking = Booking.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();

        bookingOutDto = BookingOutDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .booker(userDto)
                .item(itemOutDto)
                .build();
    }

    @Test
    void addBookingShouldReturn200AndBookingOutDto() throws Exception {
        when(bookingService.addBooking(anyLong(), anyLong(),any(Booking.class)))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class));

        verify(bookingService, times(1))
                .addBooking(1L, 1L, BookingMapper.toBooking(bookingDto));
    }

    @Test
    void approveBookingShouldReturn200AndBookingOutDto() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).approveBooking(1L, bookingId, true);
    }

    @Test
    void getBookingByIdShouldReturn200AndBookingOutDto() throws Exception {
        when(bookingService.getBooking(anyLong(),anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), Status.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class));

        verify(bookingService, times(1)).getBooking(1L, bookingId);
    }

    @Test
    void getAllUserBookingsShouldReturn200AndBookingOutDtoList() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(BookingMapper.toBookingOutDto(booking)))));

        verify(bookingService, times(1))
                .getAllUserBookings(1L, "ALL", 0, 10);
    }

    @Test
    void getAllOwnerItemBookingsShouldReturn200AndBookingOutDtoList() throws Exception {
        when(bookingService.getAllOwnerItemBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(BookingMapper.toBookingOutDto(booking)))));

        verify(bookingService, times(1))
                .getAllOwnerItemBookings(1L, "ALL", 0, 10);
    }

}
