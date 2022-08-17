package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Long userId);

    Item updateItem(Item item, Long userId, Long itemId);

    Item getItemById(Long itemId);

    List<Item> getAllItemsByUser(Long userId);

    List<Item> searchItemsByDescription(String text);
}