package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto addBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                    @Valid @RequestBody BookingDto bookingDto
    ) {
        Booking booking = bookingService.addBooking(userId, BookingMapper.toBooking(bookingDto));
        log.info("Пользователь {} запросил бронирование вещи: {}", userId, bookingDto.getItemId());
        return BookingMapper.toBookingOutDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approveBooking(@RequestHeader(X_SHARED_USER_ID) Long ownerId,
                                        @Positive @PathVariable Long bookingId,
                                        @RequestParam Boolean approved
    ) {
        Booking booking = bookingService.approveBooking(ownerId, bookingId, approved);
                log.info("Пользователь {} реагирует на запрос вещи: {}", ownerId, bookingId);
        return BookingMapper.toBookingOutDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                    @Positive @PathVariable Long bookingId
    ) {
        Booking booking = bookingService.getBooking(userId, bookingId);
        log.info("Пользователь {} запрашивает данные о бронировании: {}", userId, bookingId);
        return BookingMapper.toBookingOutDto(booking);
    }

    @GetMapping
    public List<BookingOutDto> getAllUserBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("Пользователь {} запросил список своих бронирований в статусе: {}", userId, state);
        return bookingService.getAllUserBookings(userId, state).stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllOwnerItemBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("Пользователь {} запросил список своих вещей в статусе бронирования: {}", userId, state);
        return bookingService.getAllOwnerItemBookings(userId, state).stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList());
    }

}
