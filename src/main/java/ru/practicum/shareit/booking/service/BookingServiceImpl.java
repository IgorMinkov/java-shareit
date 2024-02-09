package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.service.ItemService;
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
    public Booking addBooking(Long userId, Booking booking) {
        checkUser(userId);
        checkItem(booking.getItemId());
        ItemOutDto itemOutDto = itemService.getById(booking.getItemId(), userId);
//        if (!Objects.equals(itemOutDto.getId(), userId)) {
//            throw new DataNotFoundException(
//                    String.format("Пользователь %s не владелец предмета c id: %s", userId, userId)
//            );
//        }
        booking.setBookerId(userId);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approveBooking(Long ownerId, Long bookingId, Boolean approved) {
        checkUser(ownerId);
        checkBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();

        if (!Objects.equals(booking.getItemId(), ownerId)) {
            throw new DataNotFoundException(
                    String.format("Пользователь %s не владелец предмета бронирования", ownerId));
        }
        if (approved) {
            if (booking.getStatus().equals(Status.APPROVED)) {
                throw new ValidationException("Бронирование уже подтверждено");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        checkBooking(bookingId);
        checkUser(userId);
        Booking booking = bookingRepository.findById(bookingId).get();
        if (Objects.equals(booking.getBookerId(), userId) || Objects.equals(booking.getId(), userId)) { // нужна ссылка на item в модели
            return booking;
        } else {
            throw new DataNotFoundException("Только владелец вещи или автор бронирования" +
                    " могут получить данные о бронировании");
        }
    }

    @Override
    public List<Booking> getAllUserBookings(Long userId, String state) {
        checkUser(userId);
        List<Booking> bookings = new ArrayList<>();

        State bookingState = State.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED);
                break;

        }
        return bookings;
    }

    @Override
    public List<Booking> getAllOwnerItemBookings(Long userId, String state) {
        checkUser(userId);
        if (itemService.getOwnerItems(userId).isEmpty()) {
            throw new ValidationException(
                    String.format("Не найдень предметов для бронирования у пользователя c id: %s", userId));
        }
        List<Booking> bookings = new ArrayList<>();

        State bookingState = State.getEnumValue(state);
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                        userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, Status.REJECTED);
                break;
        }
        return bookings;
    }

    @Override
    public void checkBooking(Long id) {
        if(bookingRepository.existsById(id)) {
            throw new DataNotFoundException(String.format("Не найдено бронирование c id: %s", id));
        }
    }

    private void checkUser(Long id) {
        userService.checkUser(id);
    }

    private void checkItem(Long id) {
        itemService.checkItem(id);
    }

}
