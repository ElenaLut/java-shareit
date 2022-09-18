package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";
    private static final String DEFAULT_VALUE_FROM = "0";
    private static final String DEFAULT_VALUE_SIZE = "10";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader(name = USER_IN_HEADER) Long userId) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new IncorrectRequestException("Не указано описание");
        }
        ItemRequest itemRequest = ItemRequestsMapper.toItemRequest(itemRequestDto);
        return ItemRequestsMapper.toItemRequestDto(itemRequestService.createRequest(itemRequest, userId));
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader(USER_IN_HEADER) Long userId,
                                             @PathVariable("requestId") Long itemRequestId) {
        return itemRequestService.getRequestDtoById(itemRequestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllItemRequestsByUser(@RequestHeader(USER_IN_HEADER) Long userId) {
        return itemRequestService.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestParam(defaultValue = DEFAULT_VALUE_FROM, value = "from", required = false)
                                       @PositiveOrZero int fromList,
                                       @RequestParam(defaultValue = DEFAULT_VALUE_SIZE, required = false)
                                       @Positive int size,
                                       @RequestHeader(USER_IN_HEADER) Long userId) {
        return itemRequestService.getAllRequests(fromList, size, userId);
    }

    @DeleteMapping("/{requestId}")
    public void deleteById(@PathVariable("requestId") Long requestId) {
        itemRequestService.deleteRequestById(requestId);
    }
}