package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingControllerTests.BOOKING_END;
import static ru.practicum.shareit.booking.BookingControllerTests.BOOKING_START;

@SpringBootTest
public class BookingServiceTests {

    @Autowired
    private BookingService bookingService;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item;
    private Booking bookingFromDto;
    private Booking booking;
    private Long bookingId;
    private Long bookerId;
    private Long ownerId;

    @BeforeEach
    void setUp() {
        bookingId = 1L;
        ownerId = 1L;
        bookerId = 2L;

        owner = User.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        booker = User.builder()
                .id(2L)
                .name("Boris")
                .email("Boris@yandex.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("itemName")
                .description("item description")
                .available(true)
                .owner(owner)
                .build();

        booking = Booking.builder()
                .id(bookingId)
                .start(BOOKING_START)
                .end(BOOKING_END)
                .item(item)
                .booker(booker)
                .status(Status.APPROVED)
                .build();

        bookingFromDto = Booking.builder()
                .start(BOOKING_START)
                .end(BOOKING_END)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void  addBooking() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking testBooking = bookingService.addBooking(bookerId, 1L, bookingFromDto);

        assertEquals(testBooking.getItem(), item);
        assertEquals(testBooking.getBooker(), booker);
        assertEquals(testBooking.getStatus(), booking.getStatus());
        assertEquals(testBooking.getStart(), bookingFromDto.getStart());
        assertEquals(testBooking.getEnd(), bookingFromDto.getEnd());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void  addBookingByOwnerFail() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(DataNotFoundException.class, () -> bookingService.addBooking(ownerId,1L, bookingFromDto));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void  addBookingForNotAvailableItemFail() {
        item.setAvailable(false);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookerId, 1L, bookingFromDto));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void  addBookingEndIsBeforeStartFail() {
        bookingFromDto.setEnd(BOOKING_END.minusDays(3));

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookerId, 1L, bookingFromDto));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void  addBookingEndIsEqualsStartFail() {
        bookingFromDto.setEnd(BOOKING_START);

        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThrows(ValidationException.class, () -> bookingService.addBooking(bookerId, 1L, bookingFromDto));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking testBooking = bookingService.getById(bookingId);

        assertEquals(testBooking.getId(), booking.getId());
        assertEquals(testBooking.getItem(), booking.getItem());
        assertEquals(testBooking.getBooker(), booking.getBooker());
        assertEquals(testBooking.getStatus(), booking.getStatus());

        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void getBookingByWrongIdFail() {
        when(bookingRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> bookingService.getById(-1L));

        verify(bookingRepository, times(1)).findById(-1L);
    }

    @Test
    void getBookingByNotBookerOrNotOwnerFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(DataNotFoundException.class, () -> bookingService.getBooking(3L, bookingId));

        verify(userRepository, times(1)).existsById(3L);
        verify(bookingRepository, times(1)).findById(bookingId);
    }

    @Test
    void  approveBooking() {
        booking.setStatus(Status.WAITING);

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        Booking testApproveBooking = bookingService.approveBooking(ownerId, bookingId, true);
        assertEquals(testApproveBooking.getStatus(), Status.APPROVED);
        verify(bookingRepository, times(1)).save(testApproveBooking);

        booking.setStatus(Status.WAITING);

        Booking testRejectBooking = bookingService.approveBooking(ownerId, bookingId, false);
        assertEquals(testRejectBooking.getStatus(), Status.REJECTED);
        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void  approveApprovedBookingFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(ownerId, bookingId, true));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void  approveBookingNotByItemOwnerFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(DataNotFoundException.class,
                () -> bookingService.approveBooking(bookerId, bookingId, true));
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void getAllUserBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        String state = "ALL";
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> testBookingList = bookingService.getAllUserBookings(bookerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getBooker(), booker);

        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(bookerId, pageable);

        state = "CURRENT";
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllUserBookings(bookerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getBooker(), booker);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));

        state = "PAST";
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllUserBookings(bookerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getBooker(), booker);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class));

        state = "FUTURE";
        when(bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllUserBookings(bookerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getBooker(), booker);

        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfterOrderByStartDesc(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class));

        state = "WAITING";
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllUserBookings(bookerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getBooker(), booker);

        state = "REJECTED";
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllUserBookings(bookerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getBooker(), booker);

        verify(bookingRepository, times(2))
                .findAllByBookerIdAndStatusOrderByStartDesc(
                        anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllOwnerItemBookings() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(item)));

        String state = "ALL";
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        List<Booking> testBookingList = bookingService.getAllOwnerItemBookings(ownerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getItem().getOwner(), owner);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdOrderByStartDesc(
                        anyLong(), any(Pageable.class));

        state = "CURRENT";
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllOwnerItemBookings(ownerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getItem().getOwner(), owner);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartAsc(
                        anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class));

        state = "PAST";
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllOwnerItemBookings(ownerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getItem().getOwner(), owner);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class));

        state = "FUTURE";
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllOwnerItemBookings(ownerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getItem().getOwner(), owner);

        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
                        anyLong(), any(LocalDateTime.class), any(Pageable.class));

        state = "WAITING";
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllOwnerItemBookings(ownerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getItem().getOwner(), owner);

        state = "REJECTED";
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(
                anyLong(), any(Status.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(booking)));

        testBookingList = bookingService.getAllOwnerItemBookings(ownerId, state, 0, 10);

        assertEquals(testBookingList.get(0).getId(), booking.getId());
        assertEquals(testBookingList.get(0).getStatus(), booking.getStatus());
        assertEquals(testBookingList.get(0).getItem().getOwner(), owner);

        verify(bookingRepository, times(2))
                .findAllByItemOwnerIdAndStatusOrderByStartDesc(
                        anyLong(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllItemBookingsForNotHaveItemsOwnerFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerId(anyLong(), any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        assertThrows(ValidationException.class, () -> bookingService.getAllOwnerItemBookings(
                bookerId, "ALL", 0, 10));
    }

}
