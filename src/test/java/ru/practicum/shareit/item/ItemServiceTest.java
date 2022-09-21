package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.CommentRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    @Mock
    UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    private final User user = new User(1L, "name", "user@user.ru");
    private final Item item = new Item(1L, "name", "description", true, user, 1L, null);
    private final Comment comment = new Comment(1L, "comment", null, user, LocalDateTime.MIN);

    @BeforeEach
    void beforeEach() {
        itemService = new ItemServiceImpl(itemRepository, userService, commentRepository, bookingRepository);
    }

    @Test
    void createItemTest() {
        Item itemForCreate = new Item(1L, "name", "description", true, null, null, null);
        Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);
        Item result = itemService.createItem(itemForCreate, user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(item, result);
    }

    @Test
    void updateItemTest() {
        Item itemUpdate = new Item(1L, "nameUpdate", "descriptionUpdate", true, user, 1L, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(itemUpdate)).thenReturn(itemUpdate);
        Item result = itemService.updateItem(itemUpdate, 1L);
        Assertions.assertNotNull(result);
        assertThat(itemUpdate.getId(), equalTo(result.getId()));
        assertThat(itemUpdate.getName(), equalTo(result.getName()));
        assertThat(itemUpdate.getDescription(), equalTo(item.getDescription()));
        assertThat(itemUpdate.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemUpdate.getOwner(), equalTo(item.getOwner()));
        assertThat(itemUpdate.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    void updateItemWithIncorrectUserTest() {
        Item updateItem = new Item(1L, "name", "description", true, null, null, null);
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(updateItem, 100L));
    }

    @Test
    void getItemByIdTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Item result = itemService.getItemById(item.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(item, result);
    }

    @Test
    void getItemByWrongIdTest() {
        Mockito.when(itemRepository.findById(100L)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(100L));
    }

    @Test
    void getItemDtoByIdTest() {
        Mockito.when(itemRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllCommentsByItemId(Mockito.anyLong())).thenReturn(List.of(comment));
        Item result = ItemMapper.toItem(itemService.getItemDtoById(1L, 1L));
        result.setOwner(user);
        Assertions.assertNotNull(result);
        assertThat(item.getId(), equalTo(result.getId()));
        assertThat(item.getName(), equalTo(result.getName()));
        assertThat(item.getDescription(), equalTo(item.getDescription()));
        assertThat(item.getAvailable(), equalTo(item.getAvailable()));
        assertThat(item.getOwner(), equalTo(item.getOwner()));
        assertThat(item.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    void getCommentsByItemTest() {
        Mockito.when(commentRepository.findAllCommentsByItemId(1L)).thenReturn(List.of(comment));
        List<Comment> result = itemService.getCommentsByItem(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(List.of(comment), result);
    }

    @Test
    void addCommentTest() {
        Booking booking = new Booking();
        Mockito.when(bookingRepository.getCompletedBooking(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(LocalDateTime.class))).thenReturn(booking);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(comment);
        Comment result = itemService.addComment(1L, 1L, comment);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(comment, result);
    }

    @Test
    void searchItemsByDescriptionTest() {
        Item itemForSearch = new Item(1L, "name", "description", true, null, 1L, null);
        Mockito.when(itemRepository.searchItemsByTextInNameAndDescription("text", PageRequest.of(0, 10)))
                .thenReturn((List.of(item)));
        List<Item> itemList = itemService.searchItemsByDescription(0, 10, "text")
                .stream().map(ItemMapper::toItem).collect(Collectors.toList());
        Assertions.assertNotNull(itemList);
        Assertions.assertEquals(1, itemList.size());
        assertThat(itemForSearch.getId(), equalTo(itemList.get(0).getId()));
        assertThat(itemForSearch.getName(), equalTo(itemList.get(0).getName()));
        assertThat(itemForSearch.getAvailable(), equalTo(itemList.get(0).getAvailable()));
        assertThat(itemForSearch.getDescription(), equalTo(itemList.get(0).getDescription()));
    }
}