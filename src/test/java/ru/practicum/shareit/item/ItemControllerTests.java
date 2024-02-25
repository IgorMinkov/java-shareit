package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.item.ItemController.X_SHARED_USER_ID;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private Item item;
    private ItemOutDto itemOutDto;
    private ItemRequest itemRequest;
    private Long itemId;
    private CommentDto commentDto;
    private Comment comment;
    private CommentOutDto commentOutDto;

    @BeforeEach
    void setUp() {

        itemId = 1L;

        User user = User.builder()
                .id(1L)
                .name("Alex")
                .email("alexFirst@yandex.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("item request 1")
                .created(LocalDateTime.now())
                .build();

        itemDto = ItemDto.builder()
                .id(itemId)
                .name("itemName")
                .description("item description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        item = Item.builder()
                .id(itemId)
                .name("itemName")
                .description("item description")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        commentDto = new CommentDto();
        commentDto.setText("first!");

        comment = Comment.builder()
                .id(1L)
                .text("first!")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        commentOutDto = CommentOutDto.builder()
                .id(1L)
                .text("first!")
                .authorName(user.getName())
                .created(comment.getCreated())
                .build();

        itemOutDto = ItemOutDto.builder()
                .id(itemId)
                .name("itemName")
                .description("item description")
                .available(true)
                .comments(List.of(commentOutDto))
                .requestId(itemRequest.getId())
                .build();
    }

    @Test
    void addItem() throws Exception {
        when(itemService.create(anyLong(), any(Item.class), anyLong()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1))
                .create(1L, ItemMapper.toItem(itemDto), itemRequest.getId());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(any(Item.class), anyLong(), anyLong()))
                .thenReturn(item);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId()), Long.class));

        verify(itemService, times(1)).update(ItemMapper.toItem(itemDto), itemId, 1L);
    }

    @Test
    void getItemById() throws Exception {
        when(itemService.getById(anyLong()))
                .thenReturn(item);
        when(itemService.addBookingAndComments(any(Item.class), anyLong()))
                .thenReturn(itemOutDto);

        mvc.perform(get("/items/{itemId}", itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemOutDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemOutDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemOutDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.requestId", is(itemOutDto.getRequestId()), Long.class));

        verify(itemService, times(1)).getById(itemId);
        verify(itemService, times(1)).addBookingAndComments(item, 1L);
    }

    @Test
    void getAllOwnerItems() throws Exception {
        when(itemService.getOwnerItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        when(itemService.addBookingAndComments(any(Item.class), anyLong()))
                .thenReturn(itemOutDto);

        mvc.perform(get("/items")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemOutDto))));

        verify(itemService, times(1)).getOwnerItems(1L, 0, 10);
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

        verify(itemService, times(1)).searchItems("text", 0, 10);
    }

    @Test
    void deleteItemShouldReturn200() throws Exception {
        mvc.perform(delete("/items/{itemId}", itemId)
                .header(X_SHARED_USER_ID, 1L))
        .andExpect(status().isOk());

        verify(itemService, times(1)).delete(1L, itemId);
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyLong(), any(Comment.class), anyLong()))
                .thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(X_SHARED_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(commentOutDto)));

        verify(itemService, times(1))
                .addComment(1L, CommentMapper.toComment(commentDto), itemId);
    }

}
