package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DataNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public ItemRequest addRequest(ItemRequest itemRequest, Long userId) {
        User user = userService.getById(userId);
        itemRequest.setRequester(user);
        return requestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        checkUser(userId);
        return requestRepository.findByRequesterIdOrderByCreatedAsc(userId);
    }

    @Override
    public List<ItemRequest> getAllRequests(Long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public ItemRequest getRequest(Long userId, Long requestId) {
        checkUser(userId);
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new DataNotFoundException(String.format("Не найден запрос c id: %s", requestId)));

    }

    @Override
    public ItemRequestOutDto addItems(ItemRequest itemRequest) {
        return null;
    }

    private void checkUser(Long id) {
        userService.checkUser(id);
    }

}
