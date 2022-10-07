package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createRequest(ItemRequest itemRequest, Long userId);

    void deleteRequestById(Long itemRequestId);

    ItemRequest getRequestById(Long itemRequestId);

    ItemRequestDto getRequestDtoById(Long itemRequestId, Long userId);

    List<ItemRequestDto> getAllRequests(int fromLine, int size, Long userId);

    List<ItemRequestDto> getRequestsByUserId(Long userId);

}
