package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";
    private static final String DEFAULT_VALUE_FROM = "0";
    private static final String DEFAULT_VALUE_SIZE = "10";

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody ItemDto itemDto,
                                             @RequestHeader(name = USER_IN_HEADER) Long userId) {
        log.info("Запрос на создание вещи {}, пользователем с id {}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestHeader(name = USER_IN_HEADER) Long userId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Запрос на обновление вещи {}, пользователем с id {}", itemDto, userId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_IN_HEADER) Long userId,
                                              @PathVariable("itemId") Long itemId) {
        log.info("Запрос на получение вещи {}, пользователем с id {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM, required = false)
                                                    @PositiveOrZero int fromLine,
                                                    @RequestParam(defaultValue = DEFAULT_VALUE_SIZE, required = false) @Positive int size,
                                                    @RequestHeader(USER_IN_HEADER) Long userId) {
        log.info("Запрос на получение вещей, пользователем с id {}", userId);
        return itemClient.getAllItems(userId, fromLine, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByDescription(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM, required = false)
                                                           @PositiveOrZero int fromLine,
                                                           @Positive @RequestHeader(USER_IN_HEADER) Long userId,
                                                           @RequestParam(defaultValue = DEFAULT_VALUE_SIZE, required = false) @Positive int size,
                                                           @RequestParam(name = "text") String text) {
        log.info("Запрос на поиск вещей по описанию {}", text);
        return itemClient.searchItems(userId, text, fromLine, size);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestBody @Valid CommentDto commentDto,
                                              @RequestHeader(name = USER_IN_HEADER) Long userId,
                                              @PathVariable Long itemId) {
        log.info("Запрос на добавление комментария {} к вещи {}, пользователем с id {}", commentDto, itemId, userId);
        return itemClient.addComment(userId, commentDto, itemId);
    }
}