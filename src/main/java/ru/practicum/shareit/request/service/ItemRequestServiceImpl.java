package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    @Override
    public ItemRequest addRequest(ItemRequest itemRequest, Long userId) {
        return null;
    }

    @Override
    public List<ItemRequest> getUserRequests(Long userId) {
        return null;
    }

    @Override
    public List<ItemRequest> getAllRequests(Long userId, Integer from, Integer size) {
        return null;
    }

    @Override
    public ItemRequest getRequest(Long userId, Long requestId) {
        return null;
    }

    @Override
    public ItemRequestOutDto addItems(ItemRequest itemRequest) {
        return null;
    }
}
