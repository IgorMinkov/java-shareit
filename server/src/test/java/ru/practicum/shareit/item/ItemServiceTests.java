package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingControllerTests.BOOKING_END;
import static ru.practicum.shareit.booking.BookingControllerTests.BOOKING_START;

@SpringBootTest
public class ItemServiceTests {

    @Autowired
    private ItemService itemService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private User owner;
    private Item item;
    private Item itemFromDto;
    private Comment comment;
    private Comment commentFromDto;
    private ItemRequest itemRequest;
    private Booking booking;
    private Long itemId;

    @BeforeEach
    void setUp() {
        itemId = 1L;

        user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Boris")
                .email("Boris@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("item request 1")
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        itemFromDto = Item.builder()
                .id(itemId)
                .name("itemName")
                .description("item description")
                .available(true)
                .build();

        item = Item.builder()
                .id(itemId)
                .name("itemName")
                .description("item description")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("first!")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentFromDto = Comment.builder()
                .text("first!")
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(BOOKING_START)
                .end(BOOKING_END)
                .item(item)
                .booker(user)
                .status(Status.APPROVED)
                .build();
    }

    @Test
    void addItemWithoutRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item testItem = itemService.create(owner.getId(), itemFromDto, null);

        assertEquals(testItem.getId(), itemFromDto.getId());
        assertEquals(testItem.getDescription(), itemFromDto.getDescription());
        assertEquals(testItem.getOwner(), owner);

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addItemWithRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item testItem = itemService.create(owner.getId(), itemFromDto, itemRequest.getId());

        assertEquals(testItem.getId(), itemFromDto.getId());
        assertEquals(testItem.getDescription(), itemFromDto.getDescription());
        assertEquals(testItem.getOwner(), owner);
        assertEquals(testItem.getRequest(), itemRequest);

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void addItemWithNotFoundRequestFail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemService.create(owner.getId(), itemFromDto, -1L));

        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void updateItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item testItem = itemService.update(itemFromDto, itemId, owner.getId());

        assertEquals(testItem.getId(), itemFromDto.getId());
        assertEquals(testItem.getName(), itemFromDto.getName());
        assertEquals(testItem.getDescription(), itemFromDto.getDescription());
        assertEquals(testItem.getAvailable(), itemFromDto.getAvailable());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemNotByOwnerFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(DataNotFoundException.class, () -> itemService.update(itemFromDto, itemId, user.getId()));

        verify(itemRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void searchItems() {
        when(itemRepository.search(anyString(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(item)));

        List<Item> testItemList = itemService.searchItems("text", 0, 10);

        assertEquals(testItemList.get(0).getId(), item.getId());
        assertEquals(testItemList.get(0).getDescription(), item.getDescription());
        assertEquals(testItemList.get(0).getAvailable(), item.getAvailable());
        assertEquals(testItemList.get(0).getRequest(), item.getRequest());

        verify(itemRepository, times(1)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void searchItemsBlankText() {
        List<Item> testItemList = itemService.searchItems("     ", 0, 10);

        assertTrue(testItemList.isEmpty());

        verify(itemRepository, times(0)).search(anyString(), any(PageRequest.class));
    }

    @Test
    void deleteItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        itemService.delete(owner.getId(), itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
        assertFalse(itemRepository.existsById(itemId));
    }

    @Test
    void deleteItemNotByOwnerFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(DataNotFoundException.class, () -> itemService.delete(user.getId(), itemId));

        verify(itemRepository, times(0)).deleteById(itemId);
    }

    @Test
    void addComment() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment testComment = itemService.addComment(user.getId(), commentFromDto, itemId);

        assertEquals(testComment.getId(), comment.getId());
        assertEquals(testComment.getText(), comment.getText());
        assertEquals(testComment.getAuthor(), user);
        assertEquals(testComment.getItem(), item);

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addCommentUserNotBookingItem() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                anyLong(), anyLong(), any(Status.class), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService.addComment(99L, commentFromDto, itemId));

        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    void addBookingAndComments() {
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                anyLong(), any(Status.class), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(List.of(comment));

        ItemOutDto testOutDto = itemService.addBookingAndComments(item, owner.getId());
        ItemOutDto itemOutDto = ItemMapper.toItemOutDto(item);

        assertEquals(itemOutDto.getId(), testOutDto.getId());
        assertEquals(itemOutDto.getName(), testOutDto.getName());
        assertEquals(itemOutDto.getDescription(), testOutDto.getDescription());
        assertEquals(itemOutDto.getAvailable(), testOutDto.getAvailable());
        assertEquals(BookingMapper.toBookingShortDto(booking), testOutDto.getLastBooking());
        assertEquals(BookingMapper.toBookingShortDto(booking), testOutDto.getNextBooking());
        assertEquals(CommentMapper.toCommentOutDto(comment), testOutDto.getComments().get(0));
    }

    @Test
    void addBookingAndCommentsForNoOwnerAndNoComments() {
        when(commentRepository.findAllByItemId(anyLong())).thenReturn(Collections.emptyList());

        ItemOutDto testOutDto = itemService.addBookingAndComments(item, user.getId());
        ItemOutDto itemOutDto = ItemMapper.toItemOutDto(item);

        assertEquals(itemOutDto.getId(), testOutDto.getId());
        assertEquals(itemOutDto.getName(), testOutDto.getName());
        assertEquals(itemOutDto.getDescription(), testOutDto.getDescription());
        assertEquals(itemOutDto.getAvailable(), testOutDto.getAvailable());
        assertNull(testOutDto.getLastBooking());
        assertTrue(testOutDto.getComments().isEmpty());

        verify(bookingRepository, times(0))
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                        anyLong(), any(Status.class), any(LocalDateTime.class));

        verify(bookingRepository, times(0))
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        anyLong(), any(Status.class), any(LocalDateTime.class));

        verify(commentRepository, times(1)).findAllByItemId(anyLong());
    }

    @Test
    void checkItemFail() {
        when(itemRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(DataNotFoundException.class, () -> itemService.checkItem(-1L));

        verify(itemRepository, times(1)).existsById(-1L);
    }

    @Test
    void getItemByRequestIdWithNoRequests() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        List<Item> testItemList = itemService.getByRequestId(99L);
        assertTrue(testItemList.isEmpty());

        verify(itemRepository, times(1)).findAllByRequestId(99L);
    }

}
