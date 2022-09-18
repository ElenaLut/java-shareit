package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
public class RequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    private static final String USER_IN_HEADER = "X-Sharer-User-Id";

    private final User user1 = new User(1L, "name1", "user1@user.ru");
    private final User user2 = new User(2L, "name2", "user2@user.ru");
    private final ItemDto itemDto = new ItemDto(1L, "name", "description", true, user1, 1L, null, null, null);
    private final ItemRequest itemRequest = new ItemRequest(1L, "description", user2, null);
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "description", 2L, null, List.of(itemDto));
    private final ItemRequest itemRequestNew = new ItemRequest(1L, "description", null, null);

    @BeforeEach
    void beforeEach(WebApplicationContext wac) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .build();
    }

    @Test
    void saveRequestTest() throws Exception {
        Mockito.when(itemRequestService.createRequest(Mockito.any(ItemRequest.class), Mockito.anyLong()))
                .thenReturn(itemRequest);
        mockMvc.perform(post("/requests")
                        .header(USER_IN_HEADER, 2L)
                        .content(mapper.writeValueAsString(itemRequestNew))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requester").value(itemRequestDto.getRequester()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated()));
    }

    @Test
    void deleteRequestByIdTest() throws Exception {
        mockMvc.perform(delete("/requests/1")).andExpect(status().isOk());
    }

    @Test
    void getRequestByIdTest() throws Exception {
        Mockito.when(itemRequestService.getRequestDtoById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(itemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.requester").value(itemRequestDto.getRequester()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated()));
    }

    @Test
    void getRequestByIncorrectIdTest() throws Exception {
        Mockito.when(itemRequestService.getRequestDtoById(Mockito.anyLong(), Mockito.anyLong())).thenThrow(NotFoundException.class);
        mockMvc.perform(get("/requests/1")
                        .header(USER_IN_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRequestsTest() throws Exception {
        Mockito.when(itemRequestService.getAllRequests(1, 1, 2L)).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests/all?from=1&size=1")
                        .header(USER_IN_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requester").value(itemRequestDto.getRequester()))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated()));
    }

    @Test
    void getRequestsByUserIdTest() throws Exception {
        Mockito.when(itemRequestService.getRequestsByUserId(2L)).thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests")
                        .header(USER_IN_HEADER, 2L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].requester").value(itemRequestDto.getRequester()))
                .andExpect(jsonPath("$[0].created").value(itemRequestDto.getCreated()));
    }
}
