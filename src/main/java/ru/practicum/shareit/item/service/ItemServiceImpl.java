package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.CommentOutDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
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
    private final UserService userService;

    @Override
    public Item create(Long ownerId, Item item) {
        checkUser(ownerId);
        itemRepository.save(item);
        Item newItem = ItemMapper.toItem(getById(item.getId(), ownerId), ownerId);
        log.info("Добавлен предмет: {}", newItem);
        return newItem;
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) {
        checkUser(userId);
        checkItem(itemId);
        Item newItem = ItemMapper.toItem(getById(item.getId(), userId), userId);
        if (!Objects.equals(newItem.getOwnerId(), userId)) {
            throw new DataNotFoundException(
                    String.format("Пользователь %s не владелец предмета c id: %s", userId, itemId)
            );
        }
        Optional.ofNullable(item.getName()).ifPresent(newItem::setName);
        Optional.ofNullable(item.getDescription()).ifPresent(newItem::setDescription);
        Optional.ofNullable(item.getAvailable()).ifPresent(newItem::setAvailable);
        itemRepository.save(newItem);
        log.info("Обновлен предмет: {}", newItem);
        return newItem;
    }

    @Override
    public ItemOutDto getById(Long itemId, Long userId) {
        checkItem(itemId);
        checkUser(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Не найден предмет c id: %s", itemId)));
        return addBookingAndCommentsToItem(item, LocalDateTime.now());
    }

    @Override
    public List<ItemOutDto> getOwnerItems(Long userId) {
        checkUser(userId);
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<ItemOutDto> result = new ArrayList<>();
        for(Item item: userItems) {
            ItemOutDto dto = addBookingAndCommentsToItem(item, LocalDateTime.now());
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }

    @Override
    public void delete(Long ownerId, Long itemId) {
        checkUser(ownerId);
        checkItem(itemId);
        Item item = ItemMapper.toItem(getById(itemId, ownerId), ownerId);
        if (!Objects.equals(item.getOwnerId(), ownerId)) {
            throw new DataNotFoundException(
                    String.format("У пользователя %s не найден предмет c id: %s", ownerId, itemId)
            );
        }
        itemRepository.deleteById(itemId);
        log.info("Удален предмет с id: {} у пользователя с id: {}", itemId, ownerId);
    }

    @Override
    public CommentOutDto addComment(Long userId, Comment comment, Long itemId) {
        checkUser(userId);
        checkItem(itemId);
        LocalDateTime workTime = LocalDateTime.now();
        Optional<Booking> booking = bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                itemId, userId, Status.APPROVED, workTime);
        if(booking.isPresent()) {
            comment.setCreated(workTime);
            commentRepository.save(comment);
        } else {
            throw new DataNotFoundException(
                    String.format("Не найдены бронирования для комментария пользователя c id: %s", userId));
        }
        return CommentMapper.toCommentOutDto(comment);
    }

    private ItemOutDto addBookingAndCommentsToItem(Item item, LocalDateTime workTime) {
        ItemOutDto dto = ItemMapper.toItemOutDto(item);

        Optional<Booking> lastBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(dto.getId(), Status.APPROVED, workTime);
        Optional<Booking> nextBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(dto.getId(), Status.APPROVED, workTime);
        lastBooking.ifPresent(booking -> dto.setLastBooking(BookingMapper.toBookingOutDto(booking)));
        nextBooking.ifPresent(booking -> dto.setNextBooking(BookingMapper.toBookingOutDto(booking)));

        List<Comment> commentList = commentRepository.findAllByItemId(dto.getId());
        if (!commentList.isEmpty()) {
            dto.setComments(CommentMapper.toCommentOutDtoList(commentList));
        } else {
            dto.setComments(Collections.emptyList());
        }
        return dto;
    }

    @Override
    public void checkItem(Long id) {
        if(itemRepository.existsById(id)) {
            throw new DataNotFoundException(String.format("Не найден предмет c id: %s", id));
        }
    }

    private void checkUser(Long id) {
        userService.checkUser(id);
    }

}
