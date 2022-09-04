package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createRequest(ItemRequest itemRequest);

    ItemRequest updateRequest(ItemRequest itemRequestUpdate, Long itemRequestId);

    void deleteRequestById(Long itemRequestId);

    ItemRequest getRequestById(Long itemRequestId);

    List<ItemRequest> getAllRequests();

}
