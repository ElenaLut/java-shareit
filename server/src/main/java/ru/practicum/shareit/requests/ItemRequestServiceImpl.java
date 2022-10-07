package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public ItemRequest createRequest(ItemRequest itemRequest, Long userId) {
        itemRequest.setRequester(userService.getUserById(userId));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public void deleteRequestById(Long itemRequestId) {
        itemRequestRepository.deleteById(itemRequestId);
    }

    @Override
    public ItemRequest getRequestById(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId)
                .orElseThrow(() ->
                        new NotFoundException("Не найден запрос с id № " + itemRequestId));
    }

    @Override
    public ItemRequestDto getRequestDtoById(Long itemRequestId, Long userId) {
        userService.checkIfUserExists(userId);
        ItemRequest request = getRequestById(itemRequestId);
        ItemRequestDto requestDto = ItemRequestsMapper.toItemRequestDto(request);
        requestDto.setItems(itemRepository.findByRequestId(itemRequestId).stream()
                .map(ItemMapper::toItemDto).collect(Collectors.toList()));
        log.info("Запрос с id {} получен", itemRequestId);
        return requestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(int fromLine, int size, Long userId) {
        userService.checkIfUserExists(userId);
        Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(fromLine, size, sortBy);
        List<ItemRequestDto> itemRequests = itemRequestRepository.findByRequesterIdNot(userId,
                        pageable)
                .stream().map(ItemRequestsMapper::toItemRequestDto).collect(Collectors.toList());
        itemRequests.forEach(request -> request.setItems(itemRepository.findByRequestId(request.getId())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList())));
        log.info("Список запросов получен");
        return itemRequests;
    }

    @Override
    public List<ItemRequestDto> getRequestsByUserId(Long userId) {
        userService.checkIfUserExists(userId);
        Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        List<ItemRequestDto> requests = itemRequestRepository.findByRequesterId(userId, sortBy)
                .stream().map(ItemRequestsMapper::toItemRequestDto).collect(Collectors.toList());
        requests.forEach(request -> request.setItems(itemRepository.findByRequestId(request.getId())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList())));
        log.info("Список запросов получен");
        return requests;
    }
}
