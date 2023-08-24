package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestSmallDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ActiveProfiles("unit-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ItemRequestServiceImplUnitTest {

    private ItemRequestService itemRequestService;

    @Mock
    private final ItemRequestRepository itemRequestRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final ItemRepository itemRepository;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestSmallDto itemRequestSmallDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);

        user = User.builder()
                .id(1L)
                .name("name")
                .email("test@test.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .requester(user)
                .created(LocalDateTime.now())
                .description("desc")
                .build();

        itemRequestSmallDto = ItemRequestSmallDto.builder()
                .description("desc")
                .build();

        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, List.of());
    }

    @Test
    void whenCreateItemRequestIsSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto actual = itemRequestService.createItemRequest(user.getId(), itemRequestSmallDto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(itemRequestDto);
    }

    @Test
    void whenCreateItemRequestByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не найден";

        assertThatThrownBy(() -> itemRequestService.createItemRequest(999L, itemRequestSmallDto))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetRequestsByOwnerIsSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> actual = itemRequestService.getRequestsByOwner(user.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(itemRequestDto));
    }

    @Test
    void whenGetRequestsByInvalidOwnerIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не найден";

        assertThatThrownBy(() -> itemRequestService.getRequestsByOwner(999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetAllRequests() {
        ItemRequest itemRequest1 = ItemRequest.builder()
                .requester(user)
                .description("description")
                .id(2L)
                .created(LocalDateTime.now().minusHours(1))
                .build();
        Page<ItemRequest> itemRequests = new PageImpl<>(List.of(itemRequest, itemRequest1));
        when(itemRequestRepository.findByIdIsNotOrderByCreatedAsc(anyLong(), any())).thenReturn(itemRequests);

        List<ItemRequestDto> actual = itemRequestService.getAllRequests(user.getId(), 0, 3);

        assertThat(actual).usingRecursiveComparison().isEqualTo(List.of(itemRequestDto, ItemRequestMapper.toItemRequestDto(itemRequest1, List.of())));
    }

    @Test
    void whenGetRequestByIdIsSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto actual = itemRequestService.getRequestById(user.getId(), itemRequest.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(itemRequestDto);
    }

    @Test
    void whenGetRequestByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не найден";

        assertThatThrownBy(() -> itemRequestService.getRequestById(999L, itemRequest.getId()))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetRequestByInvalidRequestIdIsNotSuccess() {
        String expectedMessage = "Запроса не существует";
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> itemRequestService.getRequestById(user.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }
}
