package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository) {
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemRequest createRequest(ItemRequest itemRequest) {
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional
    public ItemRequest updateRequest(ItemRequest itemRequestUpdate, Long itemRequestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() -> new NotFoundException("Не найден запрос с id " + itemRequestId));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    @Transactional
    public void deleteRequestById(Long itemRequestId) {
        itemRequestRepository.deleteById(itemRequestId);

    }

    @Override
    public ItemRequest getRequestById(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() ->
                        new NotFoundException("Не найден запрос с id " + itemRequestId));
    }

    @Override
    public List<ItemRequest> getAllRequests() {
        return itemRequestRepository.findAll();
    }
}
