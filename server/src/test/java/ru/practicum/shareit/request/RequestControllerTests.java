package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestControllerTests {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto testDto;
    private ItemRequestOutDto testOutDto;
    private ItemRequest testItemRequest;
    private Long testItemRequestId;
    private ItemDto testItemDto;

    @BeforeEach
    void setUp() {
        testItemRequestId = 1L;

        testDto = ItemRequestDto.builder()
                .description("item request 1")
                .build();

        testItemRequest = ItemRequest.builder()
                .id(testItemRequestId)
                .description("item request 1")
                .created(LocalDateTime.now())
                .build();

        testOutDto = ItemRequestOutDto.builder()
                .id(testItemRequestId)
                .description("item request 1")
                .created(LocalDateTime.now())
                .build();

        testItemDto = ItemDto.builder()
                .id(1L)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(testItemRequestId)
                .build();
    }

    @Test
    void addRequestShouldReturn200AndItemRequestOutDto() throws Exception {
        when(itemRequestService.addRequest(any(ItemRequest.class), anyLong()))
                .thenReturn(testItemRequest);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(testDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testOutDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).addRequest(
                ItemRequestMapper.toItemRequest(testDto), 1L);
    }

    @Test
    void getRequestByIdShouldReturn200AndItemRequestOutDto() throws Exception {
        when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenReturn(testItemRequest);

        testOutDto.setItems(List.of(testItemDto));

        when(itemRequestService.addItems(testItemRequest))
                .thenReturn(testOutDto);

        mvc.perform(get("/requests/{requestId}", testItemRequestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testOutDto.getDescription()), String.class))
                .andExpect(content().json(mapper.writeValueAsString(testOutDto)));

        verify(itemRequestService, times(1)).getRequest(1L, 1L);
    }

    @Test
    void getUserRequestsShouldReturn200AndItemRequestOutDtoList() throws Exception {
        when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(testItemRequest));

        testOutDto.setItems(List.of(testItemDto));

        when(itemRequestService.addItems(testItemRequest))
                .thenReturn(testOutDto);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(testOutDto))));

        verify(itemRequestService, times(1)).getUserRequests(1L);
    }

    @Test
    void getAllRequestsShouldReturn200AndItemRequestOutDtoList() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(testItemRequest));

        testOutDto.setItems(List.of(testItemDto));

        when(itemRequestService.addItems(testItemRequest))
                .thenReturn(testOutDto);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(testOutDto))));

        verify(itemRequestService, times(1)).getAllRequests(1L, 0, 10);
    }

}
