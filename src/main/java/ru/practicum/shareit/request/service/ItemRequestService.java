package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest addRequest(ItemRequest itemRequest, Long userId);

    List<ItemRequest> getUserRequests(Long userId);

    List<ItemRequest> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequest getRequest(Long userId, Long requestId);

}
