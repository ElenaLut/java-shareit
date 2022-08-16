package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item createItem(Item item, Long userId) {
        return itemRepository.createItem(item, userId);
    }

    @Override
    public Item updateItem(Item item, Long userId, Long itemId) {
        return itemRepository.updateItem(item, userId, itemId);
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public List<Item> getAllItemsByUser(Long userId) {
        return itemRepository.getAllItemsByUser(userId);
    }

    @Override
    public List<Item> searchItemsByDescription(String text) {
        return itemRepository.searchItemsByDescription(text);
    }
}
