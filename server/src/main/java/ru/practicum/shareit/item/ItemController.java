package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.CommentMapper;
import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";
    private static final String DEFAULT_VALUE_FROM = "0";
    private static final String DEFAULT_VALUE_SIZE = "10";

    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(name = USER_IN_HEADER) Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader(name = USER_IN_HEADER) Long userId,
                              @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.updateItem(newItem, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER_IN_HEADER) Long userId,
                               @PathVariable("itemId") Long itemId) {
        return service.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM, required = false)
                                           int fromLine,
                                           @RequestParam(defaultValue = DEFAULT_VALUE_SIZE, required = false) int size,
                                           @RequestHeader(USER_IN_HEADER) Long userId) {
        return service.getAllItemsDtoByUser(fromLine, size, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByDescription(@RequestParam(value = "from", defaultValue = DEFAULT_VALUE_FROM, required = false)
                                                  int fromLine,
                                                  @RequestParam(defaultValue = DEFAULT_VALUE_SIZE, required = false) int size,
                                                  @RequestParam(name = "text") String text) {
        return service.searchItemsByDescription(fromLine, size, text);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestBody CommentDto commentDto,
                                  @RequestHeader(name = USER_IN_HEADER) Long userId,
                                  @PathVariable Long itemId) {
        Comment comment = CommentMapper.toComment(commentDto);
        return CommentMapper.toCommentDto(service.addComment(userId, itemId, comment));
    }
}