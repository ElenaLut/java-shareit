package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final String userIdInHeader = "X-Sharer-User-Id";

    private final ItemService service;

    @Autowired
    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(name = userIdInHeader) Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.createItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader(name = userIdInHeader) Long userId,
                              @RequestBody ItemDto itemDto) {
        Item newItem = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(service.updateItem(newItem, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") Long itemId) {
        return ItemMapper.toItemDto(service.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader(userIdInHeader) Long userId) {
        return service.getAllItemsByUser(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByDescription(@RequestParam(name = "text") String text) {
        return service.searchItemsByDescription(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}