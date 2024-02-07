package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.util.Optional;

@UtilityClass
public class BookingMapper {

    public static BookingOutDto toBookingDto(Booking booking) {
        return new BookingOutDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBookerId(),
                booking.getItemId());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking =  new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItemId(bookingDto.getItemId());
        Optional.ofNullable(bookingDto.getStatus()).ifPresentOrElse(booking::setStatus,
                () -> booking.setStatus(Status.WAITING));
        return booking;
    }
}
