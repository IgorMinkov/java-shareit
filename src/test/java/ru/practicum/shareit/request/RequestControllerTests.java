package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

    private Long testItemRequestId;

    private final LocalDateTime testTime = LocalDateTime.of(2024, 2, 2, 2, 2);

    @BeforeEach
    void setUp() {
        testItemRequestId = 1L;

        testDto = new ItemRequestDto();
        testDto.setDescription("item request 1");

        testOutDto = ItemRequestOutDto.builder()
                .id(testItemRequestId)
                .description("item request 1")
                .created(testTime)
                .build();
    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.addRequest(any(ItemRequest.class), anyLong()))
                .thenReturn(ItemRequestMapper.toItemRequest(testDto));

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(testDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testItemRequestDto.getDescription()), String.class));

        verify(itemRequestService, times(1)).addRequest(testItemRequestDto, 1L);
    }








}
