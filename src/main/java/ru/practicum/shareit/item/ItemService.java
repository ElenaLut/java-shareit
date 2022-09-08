package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long userId);

    Item getItemById(Long itemId);

    ItemDto getItemDtoById(Long itemId, Long userId);

    List<Comment> getCommentsByItem(Long itemId);

    List<Item> getAllItemsByUser(Long userId);

    List<ItemDto> getAllItemsDtoByUser(Long userId);

    List<Item> searchItemsByDescription(String text);

    Comment addComment(Long userId, Long itemId, Comment comment);
}