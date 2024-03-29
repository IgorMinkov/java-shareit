package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public Booking addBooking(Long userId, Long itemId, Booking booking) {
        Item item = itemService.getById(itemId);
        User user = userService.getById(userId);
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Предмет %s сейчас недоступен для брони", item.getName()));
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new DataNotFoundException("Вы пытаетесь забронировать собственную вещь");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isEqual(booking.getEnd())) {
            throw new ValidationException("Дата окончания брони не может быть раньше или совпадать с датой начала");
        }
        booking.setItem(item);
        booking.setBooker(user);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        checkUser(ownerId);
        Booking booking = getById(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new DataNotFoundException(
                    String.format("Пользователь %s не владелец предмета бронирования", ownerId));
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        checkUser(userId);
        Booking booking = getById(bookingId);
        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return booking;
        } else {
            throw new DataNotFoundException("Только владелец вещи или автор бронирования" +
                    " могут получить данные о бронировании");
        }
    }

    @Override
    public List<Booking> getAllUserBookings(Long userId, String state, Integer from, Integer size) {
        checkUser(userId);
        List<Booking> bookings = new ArrayList<>();

        State bookingState = State.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(
                        userId, PageRequest.of(from / size, size)).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)).toList();
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        Status.WAITING,
                        PageRequest.of(from / size, size)).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                        userId,
                        Status.REJECTED,
                        PageRequest.of(from / size, size)).toList();
                break;

        }
        return bookings;
    }

    @Override
    public List<Booking> getAllOwnerItemBookings(Long userId, String state, Integer from, Integer size) {
        checkUser(userId);
        int fromForOwnerItemsCheck = 0;
        if (itemService.getOwnerItems(userId, fromForOwnerItemsCheck, size).isEmpty()) {
            throw new ValidationException(
                    String.format("Не найдены предметы для бронирования у пользователя c id: %s", userId));
        }
        List<Booking> bookings = new ArrayList<>();

        State bookingState = State.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                        userId, PageRequest.of(from / size, size)).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        userId,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)).toList();
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId,
                        LocalDateTime.now(),
                        PageRequest.of(from / size, size)).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId,
                        Status.WAITING,
                        PageRequest.of(from / size, size)).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId,
                        Status.REJECTED,
                        PageRequest.of(from / size, size)).toList();
                break;
        }
        return bookings;
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("Не найдено бронирование c id: %s", id)));
    }

    private void checkUser(Long id) {
        userService.checkUser(id);
    }

}
