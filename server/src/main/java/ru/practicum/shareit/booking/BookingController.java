package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingOutDto> addBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                    @RequestBody BookingDto bookingDto) {
        Booking booking = bookingService.addBooking(
                userId, bookingDto.getItemId(), BookingMapper.toBooking(bookingDto));
        log.info("Пользователь {} запросил бронирование вещи: {}", userId, bookingDto.getItemId());
        return ResponseEntity.ok(BookingMapper.toBookingOutDto(booking));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingOutDto> approveBooking(@RequestHeader(X_SHARED_USER_ID) Long ownerId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        Booking booking = bookingService.approveBooking(ownerId, bookingId, approved);
        log.info("Пользователь {} реагирует на запрос вещи: {}", ownerId, bookingId);
        return ResponseEntity.ok(BookingMapper.toBookingOutDto(booking));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingOutDto> getBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                    @PathVariable Long bookingId) {
        Booking booking = bookingService.getBooking(userId, bookingId);
        log.info("Пользователь {} запрашивает данные о бронировании: {}", userId, bookingId);
        return ResponseEntity.ok(BookingMapper.toBookingOutDto(booking));
    }

    @GetMapping
    public ResponseEntity<List<BookingOutDto>> getAllUserBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Пользователь {} запросил список своих бронирований в статусе: {}", userId, state);
        return ResponseEntity.ok(bookingService.getAllUserBookings(userId, state, from, size).stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingOutDto>> getAllOwnerItemBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                                       @RequestParam(defaultValue = "ALL") String state,
                                                                       @RequestParam(defaultValue = "0") Integer from,
                                                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Пользователь {} запросил список своих вещей в статусе бронирования: {}", userId, state);
        return ResponseEntity.ok(bookingService.getAllOwnerItemBookings(userId, state, from, size).stream()
                .map(BookingMapper::toBookingOutDto)
                .collect(Collectors.toList()));
    }

}
