package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RequestServiceTests {

    @Autowired
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestRepository itemRequestRepository;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private Item item;
    private ItemRequest requestFromDto;
    private ItemRequest itemRequest;
    private Long itemRequestId;

    @BeforeEach
    void setUp() {
        itemRequestId = 1L;

        user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        User owner = User.builder()
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

        requestFromDto = new ItemRequest();
        requestFromDto.setDescription("request for itemName at test");

        itemRequest = ItemRequest.builder()
                .id(itemRequestId)
                .description("request for itemName at test")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void addRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest testItemRequest = itemRequestService.addRequest(requestFromDto, user.getId());

        assertEquals(testItemRequest.getId(), itemRequest.getId());
        assertEquals(testItemRequest.getDescription(), itemRequest.getDescription());
        assertNotEquals(null, testItemRequest.getCreated());

        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByRequesterIdOrderByCreatedAsc(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequest> testItemRequestList = itemRequestService.getUserRequests(user.getId());

        assertEquals(1, testItemRequestList.size());
        assertEquals(testItemRequestList.get(0).getId(), itemRequest.getId());
        assertEquals(testItemRequestList.get(0).getDescription(), itemRequest.getDescription());

        verify(itemRequestRepository, times(1)).findByRequesterIdOrderByCreatedAsc(user.getId());
    }

    @Test
    void getAllRequests() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));

        List<ItemRequest> testItemRequestList = itemRequestService.getAllRequests(user.getId(), 0, 10);

        assertEquals(1, testItemRequestList.size());
        assertEquals(testItemRequestList.get(0).getId(), itemRequest.getId());
        assertEquals(testItemRequestList.get(0).getDescription(), itemRequest.getDescription());

        verify(itemRequestRepository, times(1))
                .findByIdIsNotOrderByCreatedAsc(anyLong(), any(PageRequest.class));
    }

    @Test
    void getRequestById() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequest testItemRequest = itemRequestService.getRequest(user.getId(), itemRequestId);

        assertEquals(testItemRequest.getId(), itemRequest.getId());
        assertEquals(testItemRequest.getDescription(), itemRequest.getDescription());

        verify(itemRequestRepository, times(1)).findById(itemRequestId);
    }

    @Test
    void getRequestByWrongIdFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenThrow(DataNotFoundException.class);

        assertThrows(DataNotFoundException.class, () -> itemRequestService.getRequest(user.getId(),-1L));

        verify(itemRequestRepository, times(1)).findById(-1L);
    }

    @Test
    void addItemsToRequest() {
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestOutDto testOutDto = itemRequestService.addItems(itemRequest);

        assertEquals(testOutDto.getId(), itemRequest.getId());
        assertEquals(testOutDto.getDescription(), itemRequest.getDescription());
        assertNotEquals(null, testOutDto.getCreated());
        assertEquals(testOutDto.getItems().get(0), ItemMapper.toItemDto(item));
    }

}
