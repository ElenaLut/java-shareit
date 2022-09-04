package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, List<Item>> items = new HashMap<>();
    private final UserService userService;
    private GeneratorItemId generatorItemId;

    @Autowired
    public ItemRepositoryImpl(UserService userService, GeneratorItemId generatorItemId) {
        this.userService = userService;
        this.generatorItemId = generatorItemId;
    }

    @Override
    public Item createItem(Item item, Long userId) {
        checkUserId(userId);
        if (item.getAvailable() == null || item.getDescription() == null || item.getName() == null ||
                item.getName().isBlank()) {
            throw new IncorrectRequestException("У предмета не указан статус");
        }
        item.setId(generatorItemId.generate());
        item.setOwnerId(userId);
        if (!items.containsKey(userId)) {
            List<Item> newItemsOfUser = new ArrayList<>();
            newItemsOfUser.add(item);
            items.put(userId, newItemsOfUser);
            return item;
        }
        List<Item> itemsOfUser = items.get(userId);
        itemsOfUser.add(item);
        items.put(userId, itemsOfUser);
        log.debug("Пользователь с id {} добавил вещь {}", userId, item.getName());
        return item;
    }

    @Override
    public Item updateItem(Item item, Long userId, Long itemId) {
        checkUserId(userId);
        Item oldItem = getItemById(itemId);
        if (oldItem.getOwnerId() != userId) {
            log.warn("Пользователь с id {} не может изменять вещь с id {}", userId, itemId);
            throw new NotFoundException("Пользователю недоступно редактирование вещи с id" + itemId);
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        log.info("Вещь с id {} обновлена", itemId);
        return oldItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        List<Item> itemsOfUser = getAllItems();
        for (Item item : itemsOfUser) {
            if (item.getId() == itemId) {
                log.info("Получена вещь с id {}.", itemId);
                return item;
            }
        }
        return null;
    }

    @Override
    public List<Item> getAllItemsByUser(Long userId) {
        checkUserId(userId);
        List<Item> itemsOfUser = items.get(userId);
        log.info("Получен список вещений пользователя с id {}", userId);
        return itemsOfUser;
    }

    @Override
    public List<Item> searchItemsByDescription(String text) {
        List<Item> matchItem = new ArrayList<>();
        if (!text.isBlank()) {
            log.info("Получены вещи, актульные для запроса {}", text);
            matchItem = getAllItems().stream()
                    .filter(item -> item.getAvailable().equals(Boolean.TRUE)
                            && (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                    .collect(Collectors.toList());
        }
        return matchItem;
    }

    private List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        items.values()
                .forEach(i -> allItems.addAll(i));
        return allItems;
    }

    private void checkUserId(long userId) {
        if (userService.getUserById(userId) == null) {
            log.error("Пользователя с id {} не существует", userId);
            throw new NotFoundException("Пользователя не существует");
        }
    }
}