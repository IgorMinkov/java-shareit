package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    Booking addBooking(Long userId, Long itemId, Booking booking);

    Booking approveBooking(Long ownerId, Long bookingId, Boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getAllUserBookings(Long userId, String state, Integer from, Integer size);

    List<Booking> getAllOwnerItemBookings(Long userId, String state, Integer from, Integer size);

    Booking getById(Long id);

}
