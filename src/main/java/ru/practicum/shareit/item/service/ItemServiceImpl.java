package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;
    private final UserService userService;

    @Override
    public Item create(Long ownerId, Item item, Long requestId) {
        User owner = userService.getById(ownerId);
        item.setOwner(owner);
        if (requestId != null) {
            ItemRequest request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new DataNotFoundException(
                            String.format("Не найден запрос c id: %s", requestId)));
            item.setRequest(request);
        }
        log.info("Добавлен предмет: {}", item);
        return itemRepository.save(item);
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        checkUser(userId);
        Item newItem = getById(itemId);
        if (!Objects.equals(newItem.getOwner().getId(), userId)) {
            throw new DataNotFoundException(
                    String.format("Пользователь %s не владелец предмета c id: %s", userId, itemId));
        }
        Optional.ofNullable(item.getName()).ifPresent(newItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(newItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(newItem::setAvailable);
        itemRepository.save(newItem);
        log.info("Обновлен предмет: {}", newItem);
        return newItem;
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Не найден предмет c id: %s", itemId)));
    }

    @Override
    public List<Item> getOwnerItems(Long userId, Integer from, Integer size) {
        checkUser(userId);
        return itemRepository.findByOwnerId(userId, PageRequest.of(from/size, size)).toList();
    }

    @Override
    public List<Item> searchItems(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, PageRequest.of(from/size, size)).toList();
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        checkUser(ownerId);
        Item item = getById(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new DataNotFoundException(
                    String.format("У пользователя %s не найден предмет c id: %s", ownerId, itemId)
            );
        }
        itemRepository.deleteById(itemId);
        log.info("Удален предмет с id: {} у пользователя с id: {}", itemId, ownerId);
    }

    @Override
    public Comment addComment(Long userId, Comment comment, Long itemId) {
        checkUser(userId);
        checkItem(itemId);
        LocalDateTime workTime = LocalDateTime.now();
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, workTime);
        if (booking.isEmpty()) {
            throw new ValidationException(
                    String.format("Не найдены бронирования для комментария пользователя c id: %s", userId));
        }
        Item item = getById(itemId);
        User user = userService.getById(userId);
        comment.setCreated(workTime);
        comment.setItem(item);
        comment.setAuthor(user);
        log.info("Добавлен комментарий: {}", comment);
        return commentRepository.save(comment);
    }

    @Override
    public ItemOutDto addBookingAndComments(Item item, Long userId) {
        LocalDateTime workTime = LocalDateTime.now();
        ItemOutDto dto = ItemMapper.toItemOutDto(item);

        if (Objects.equals(item.getOwner().getId(), userId)) {
            Optional<Booking> lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(dto.getId(), Status.APPROVED, workTime);
            Optional<Booking> nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(dto.getId(), Status.APPROVED, workTime);
            lastBooking.ifPresent(booking -> dto.setLastBooking(BookingMapper.toBookingShortDto(booking)));
            nextBooking.ifPresent(booking -> dto.setNextBooking(BookingMapper.toBookingShortDto(booking)));
        }

        List<Comment> commentList = commentRepository.findAllByItemId(dto.getId());
        if (commentList.isEmpty()) {
            dto.setComments(Collections.emptyList());
        } else {
            dto.setComments(CommentMapper.toCommentOutDtoList(commentList));
        }
        return dto;
    }

    @Override
    public void checkItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new DataNotFoundException(String.format("Не найден предмет c id: %s", id));
        }
    }

    @Override
    public List<Item> getByRequestId(Long requestId) {
        List<Item> result = itemRepository.findAllByRequestId(requestId);
        if (result.isEmpty()) {
            return Collections.emptyList();
        }
        return result;
    }

    private void checkUser(Long id) {
        userService.checkUser(id);
    }

}
