package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.util.Optional;

@UtilityClass
public class BookingMapper {

    public static BookingOutDto toBookingOutDto(Booking booking) {
        return new BookingOutDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getItem().getId(),
                booking.getBooker().getId());
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking =  new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        Optional.ofNullable(bookingDto.getStatus()).ifPresentOrElse(booking::setStatus,
                () -> booking.setStatus(Status.WAITING));
        return booking;
    }
}
