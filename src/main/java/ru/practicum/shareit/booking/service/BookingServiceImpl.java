package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Override
    public Booking addBooking(Long userId, Booking booking) {
        return null;
    }

    @Override
    public Booking approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        return null;
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        return null;
    }

    @Override
    public List<Booking> getAllUserBookings(Long userId, String state) {
        return null;
    }

    @Override
    public List<Booking> getAllOwnerItemBookings(Long userId, String state) {
        return null;
    }

}
