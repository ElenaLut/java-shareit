package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.model.Comment;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";

    private final User user1 = new User(1L, "name1", "user1@user.ru");
    private final User user2 = new User(2L, "name2", "user2@user.ru");
    private final Item item = new Item(1L, "name", "description", true, user1, 1L, null);
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, null, null, null, null, null);
    private final Comment comment = new Comment(1L, "comment", item, user2, LocalDateTime.MIN);

    @BeforeEach
    void beforeEach(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void createItemTest() throws Exception {
        Mockito.when(itemService.createItem(Mockito.any(Item.class), Mockito.anyLong())).thenReturn(item);
        mockMvc.perform(post("/items")
                        .header(USER_IN_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItemTest() throws Exception {
        Mockito.when(itemService.updateItem(Mockito.any(Item.class), Mockito.anyLong()))
                .thenReturn(item);
        mockMvc.perform(patch("/items/1")
                        .header(USER_IN_HEADER, 1L)
                        .content(mapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateItemWithIncorrectUserTest() throws Exception {
        Mockito.when(itemService.updateItem(Mockito.any(Item.class), Mockito.anyLong()))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(patch("/items/1")
                        .header(USER_IN_HEADER, 1L)
                        .content(mapper.writeValueAsString(item))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemByIdTest() throws Exception {
        Mockito.when(itemService.getItemDtoById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemDto);
        mockMvc.perform(get("/items/1")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getItemByWrongIdTest() throws Exception {
        Mockito.when(itemService.getItemDtoById(Mockito.anyLong(), Mockito.anyLong())).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/items/1")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addCommentTest() throws Exception {
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(Comment.class)))
                .thenReturn(comment);
        mockMvc.perform(post("/items/1/comment")
                        .header(USER_IN_HEADER, 1L)
                        .content(mapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comment.getId()))
                .andExpect(jsonPath("$.text").value(comment.getText()))
                .andExpect(jsonPath("$.authorName").value(comment.getAuthor().getName()));
    }

    @Test
    void searchItemsByDescriptionTest() throws Exception {
        Mockito.when(itemService.searchItemsByDescription(1, 1, "text")).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search?text=text&from=1&size=1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }
}