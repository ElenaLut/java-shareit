package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.CommentMapper;
import ru.practicum.shareit.booking.CommentRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.exception.IncorrectRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService,
                           CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Item createItem(Item item, Long userId) {
        if (item.getAvailable() == null || item.getName() == null || item.getDescription() == null
                || item.getName().isBlank()) {
            log.error("Не заполнены обязательные поля: название вещи, описание, доступность");
            throw new IncorrectRequestException("Не заполнены обязательные поля: название вещи, описание, доступность");
        }
        item.setOwner(userService.getUserById(userId));
        log.debug("Пользователь с id {} добавил вещь {}", userId, item.getName());
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item, Long userId) {
        Item oldItem = getItemById(item.getId());
        if (oldItem.getOwner().getId() != userId) {
            log.error("Пользователь с id {} не может редактировать вещь с id {}", userId, item.getId());
            throw new NotFoundException("Только владелец вещи может вносить изменение в ее описание");
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
        log.info("Вещь с id {} обновлена", item.getId());
        return oldItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() ->
                        new NotFoundException("Не найдена вещь с идентификатором № " + itemId));
    }

    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (itemDto.getOwner().getId() == userId) {
            getAllBookings(itemDto);
        }
        getCommentsByItemDto(itemDto);
        log.info("Найдена вещь с id {}.", itemId);
        return itemDto;
    }

    @Override
    public List<Comment> getCommentsByItem(Long itemId) {
        log.info("Получены комментарии для вещи с id {}.", itemId);
        return commentRepository.findAllCommentsByItemId(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsDtoByUser(int fromLine, int size, Long userId) {
        Pageable pageable = PageRequest.of(fromLine / size, size, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemDto> itemList = itemRepository.findAllByOwnerId(userId, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemList.forEach(this::getAllBookings);
        itemList.forEach(this::getCommentsByItemDto);
        log.info("Получен список вещений пользователя с id {}", userId);
        return itemList;
    }

    @Override
    public List<ItemDto> searchItemsByDescription(int fromLine, int size, String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = PageRequest.of(fromLine / size, size);
        return itemRepository.searchItemsByTextInNameAndDescription(text, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Comment addComment(Long userId, Long itemId, Comment comment) {
        Booking booking = bookingRepository.getCompletedBooking(userId, itemId, LocalDateTime.now());
        if (booking == null) {
            log.error("Бронирование не найдено");
            throw new IncorrectRequestException("Бронирование не найдено");
        }
        if (comment.getText().isBlank() || comment.getText().isEmpty()) {
            log.error("Переданный комментарий пуст");
            throw new IncorrectRequestException("Комментарий пуст");
        }
        comment.setItem(itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Не найдена вещь с идентификатором № " + itemId)));
        comment.setAuthor(userService.getUserById(userId));
        comment.setCreatedAt(LocalDateTime.now());
        log.info("Сохранен комментарий {} для вещи с id {}.", comment, itemId);
        return commentRepository.save(comment);
    }

    @Transactional
    public void getAllBookings(ItemDto itemDto) {
        if (!bookingRepository.getLastBookingByItemId(itemDto.getId(), LocalDateTime.now()).isEmpty()) {
            itemDto.setLastBooking(bookingRepository.getLastBookingByItemId(itemDto.getId(), LocalDateTime.now())
                    .stream().findFirst());
        }
        if (!bookingRepository.getNextBookingByItemId(itemDto.getId(), LocalDateTime.now()).isEmpty()) {
            itemDto.setNextBooking(bookingRepository.getNextBookingByItemId(itemDto.getId(), LocalDateTime.now())
                    .stream().findFirst());
        }
    }

    public List<Comment> getAllCommentsByItem(Long itemId) {
        log.info("Получен список комментариев о бронировании вещи № {}.", itemId);
        return commentRepository.findAllCommentsByItemId(itemId);
    }

    @Transactional
    void getCommentsByItemDto(ItemDto itemDto) {
        itemDto.setComments(getAllCommentsByItem(itemDto.getId()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        log.info("Сохранен список комментариев: {} о бронировании вещи № {}.",
                itemDto.getComments(), itemDto.getId());
    }
}