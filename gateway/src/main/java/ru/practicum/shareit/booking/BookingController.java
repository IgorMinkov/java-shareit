package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.State;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("Пользователь {} запросил бронирование вещи: {}", userId, bookingDto.getItemId());
        return bookingClient.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(X_SHARED_USER_ID) Long ownerId,
                                        @Positive @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        log.info("Пользователь {} реагирует на запрос вещи: {}", ownerId, bookingId);
        return bookingClient.approveBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                    @Positive @PathVariable Long bookingId) {
        log.info("Пользователь {} запрашивает данные о бронировании: {}", userId, bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String stateParam,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        State state = State.getEnumValue(stateParam);
        log.info("Пользователь {} запросил список своих бронирований в статусе: {}", userId, state);
        return bookingClient.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerItemBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String stateParam,
                                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        State state = State.getEnumValue(stateParam);
        log.info("Пользователь {} запросил список своих вещей в статусе бронирования: {}", userId, state);
        return bookingClient.getAllOwnerItemBookings(userId, state, from, size);
    }

}
