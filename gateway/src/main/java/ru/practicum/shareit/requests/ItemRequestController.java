package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";
    private static final String DEFAULT_VALUE_FROM = "0";
    private static final String DEFAULT_VALUE_SIZE = "10";

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader(name = USER_IN_HEADER) Long userId) {
        log.info("Запрос на создание запроса {}, пользователем с id {}", itemRequestDto, userId);
        return itemRequestClient.addRequest(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(USER_IN_HEADER) Long userId,
                                                     @PathVariable("requestId") Long itemRequestId) {
        log.info("Получение запроса {}, пользователем с id {}", itemRequestId, userId);
        return itemRequestClient.getRequest(itemRequestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestsByUser(@RequestHeader(USER_IN_HEADER) Long userId) {
        log.info("Запрос на получение всех запросов, владельцем с id ={}", userId);
        return itemRequestClient.getAllItemRequestsByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestParam(defaultValue = DEFAULT_VALUE_FROM, value = "from", required = false)
                                         @PositiveOrZero int fromList,
                                         @RequestParam(defaultValue = DEFAULT_VALUE_SIZE, required = false)
                                         @Positive int size,
                                         @RequestHeader(USER_IN_HEADER) Long userId) {
        log.info("Запрос на получение всех запросов, пользователем с id {}", userId);
        return itemRequestClient.getAllRequests(userId, fromList, size);
    }
}