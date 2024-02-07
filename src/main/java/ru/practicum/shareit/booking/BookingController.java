package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;

import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

/**
 * TODO Sprint add-bookings.
 */

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

//    private final BookingService bookingService;

    @PostMapping
    public BookingOutDto addBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                    @Valid @RequestBody BookingDto bookingDto
    ) {
        log.info("Пользователь {} запросил бронирование вещи: {}", userId, bookingDto.getItemId());
        return null;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approveBooking(@RequestHeader(X_SHARED_USER_ID) Long ownerId,
                                        @Positive @PathVariable Long bookingId,
                                        @RequestParam Boolean approved
    ) {
        log.info("Пользователь {} реагирует на запрос вещи: {}", ownerId, bookingId);
        return null;
    }

    @GetMapping("/{bookingId}")
    public BookingOutDto getBooking(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                    @Positive @PathVariable Long bookingId
    ) {
        log.info("Пользователь {} запрашивает данные о бронировании: {}", userId, bookingId);
        return null;
    }

    @GetMapping
    public List<BookingOutDto> getAllUserBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("Пользователь {} запросил список своих бронирований в статусе: {}", userId, state);
        return null;
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllOwnerItemBookings(@RequestHeader(X_SHARED_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "ALL") String state
    ) {
        log.info("Пользователь {} запросил список своих вещей в статусе бронирования: {}", userId, state);
        return null;
    }

}
