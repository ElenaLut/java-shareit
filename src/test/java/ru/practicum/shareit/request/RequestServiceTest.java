package ru.practicum.shareit.request;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.ItemRequestServiceImpl;
import ru.practicum.shareit.requests.ItemRequestsMapper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private final User user = new User(1, "user", "user@user.ru");
    private final ItemRequest request = new ItemRequest(1L, "description", user, LocalDateTime.now());

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
    }

    @Test
    void saveRequestTest() {
        Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(user);
        Mockito.when(itemRequestRepository.save(request)).thenReturn(request);
        ItemRequest result = itemRequestService.createRequest(request, 1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(request, result);
    }

    @Test
    void deleteRequestByIdTest() {
        itemRequestService.deleteRequestById(1L);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void getRequestByIdTest() {
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(request));
        ItemRequest result = itemRequestService.getRequestById(1L);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(request, result);
    }

    @Test
    void getRequestByIncorrectIdTest() {
        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(2L));
    }

    @Test
    void getAllRequestsTest() {
        Mockito.when(itemRequestRepository.findByRequesterIdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(request));
        List<ItemRequestDto> itemRequestList = itemRequestService.getAllRequests(0, 10, 1L);
        ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(request);
        Assertions.assertNotNull(itemRequestList);
        Assertions.assertEquals(itemRequestDto, itemRequestList.get(0));
    }

    @Test
    void getRequestsByUserIdTest() {
        Mockito.when(itemRequestRepository.findByRequesterId(1L, Sort.by(Sort.Direction.DESC, "createdAt")))
                .thenReturn(List.of(request));
        List<ItemRequestDto> itemRequestList = itemRequestService.getRequestsByUserId(1L);
        ItemRequestDto itemRequestDto = ItemRequestsMapper.toItemRequestDto(request);
        Assertions.assertNotNull(itemRequestList);
        Assertions.assertEquals(itemRequestDto, itemRequestList.get(0));
    }
}