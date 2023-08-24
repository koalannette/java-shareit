package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
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
public class ItemServiceImplUnitTest {

    private ItemService itemService;

    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final CommentRepository commentRepository;
    @Mock
    private final ItemRequestRepository itemRequestRepository;

    private Item item;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private User user1;
    private User user2;
    private ItemRequest itemRequest;
    private Booking booking1;
    private Booking booking2;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository);

        user1 = User.builder()
                .id(1L)
                .name("test name1")
                .email("test@test.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("test name2")
                .email("test@test.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .owner(user1)
                .description("desc")
                .name("name")
                .build();

        itemDto1 = ItemMapper.toItemDto(item);

        itemDto2 = ItemDto.builder()
                .id(2L)
                .name("item2 test")
                .description("item2 test description")
                .available(Boolean.TRUE)
                .build();

        itemRequest = ItemRequest.builder()
                .description("item request description")
                .requester(user1)
                .created(LocalDateTime.now())
                .build();

        booking1 = Booking.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(user1)
                .status(Status.WAITING)
                .id(1L)
                .build();

        booking2 = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user1)
                .status(Status.WAITING)
                .id(1L)
                .build();
    }

    @Test
    void whenCreateItemIsSuccess() {
        ItemDtoResponse expected = ItemMapper.toItemDtoResponseFromItem(item);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDtoResponse actual = itemService.createItem(user1.getId(), itemDto1);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenCreateItemByInvalidOwnerIdIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не найден";

        assertThatThrownBy(() -> itemService.createItem(999L, itemDto1))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenUpdateItemIsSuccess() {
        Item itemForUpdate = Item.builder()
                .id(1L)
                .owner(user1)
                .description("new desc")
                .available(true)
                .name("new name")
                .build();
        ItemDtoResponse expected = ItemMapper.toItemDtoResponseFromItem(itemForUpdate);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(itemForUpdate);

        ItemDtoResponse actual = itemService.updateItem(ItemMapper.toItemDto(itemForUpdate), item.getId(), user1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenUpdateItemByInvalidOwnerIdIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не является владельцем";
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.updateItem(itemDto1, item.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetItemByIdIsSuccess() {
        ItemDtoResponse expected = ItemMapper.toItemDtoResponseFromItem(item);
        expected.setLastBooking(BookingMapper.toBookingBookerDto(booking1));
        expected.setNextBooking(BookingMapper.toBookingBookerDto(booking2));
        expected.setComments(new HashSet<>());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusOrderByEndDesc(anyLong(), any(), any())).thenReturn(booking1);
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), any(), any())).thenReturn(booking2);

        ItemDtoResponse actual = itemService.getItemById(user1.getId(), item.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenGetItemByInvalidIdIsNotSuccess() {
        String expectedMessage = "Вещь с id " + 999L + " не найдена.";

        assertThatThrownBy(() -> itemService.getItemById(user1.getId(), 999L))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetItemsByUserIdIsSuccess() {
        ItemDtoResponse itemDtoResponse = ItemMapper.toItemDtoResponseFromItem(item);
        itemDtoResponse.setComments(new HashSet<>());
        List<ItemDtoResponse> expected = List.of(itemDtoResponse);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findItemByOwner_Id(anyLong(), any())).thenReturn(List.of(item));

        List<ItemDtoResponse> actual = itemService.getItemsByUserId(user1.getId(), 0, 3);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenGetItemsByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь с id " + 999L + " не найден";

        assertThatThrownBy(() -> itemService.getItemsByUserId(999L, 0, 2))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenGetItemsByText() {
        List<ItemDtoResponse> expected = List.of(ItemMapper.toItemDtoResponseFromItem(item));
        when(itemRepository.findItemByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(any(), any(), any())).thenReturn(List.of(item));

        List<ItemDtoResponse> actual = itemService.getItemsByText("text", 0, 3);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenAddCommentIsSuccess() {
        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .author(user1)
                .text("text")
                .id(1L)
                .build();
        CommentDtoResponse expected = CommentDtoResponse.builder()
                .authorName(user1.getName())
                .created(LocalDateTime.now())
                .text("text")
                .id(1L)
                .build();
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDtoResponse actual = itemService.addComment(CommentMapper.toCommentDto(comment), item.getId(), user1.getId());

        assertThat(actual).usingRecursiveComparison().ignoringFields("created").isEqualTo(expected);
    }

    @Test
    void whenAddCommentByInvalidUserIdIsNotSuccess() {
        String expectedMessage = "Пользователь  " + user1.getId() + " не арендовал вещь " + item.getId();
        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .author(user1)
                .text("text")
                .id(1L)
                .build();

        assertThatThrownBy(() -> itemService.addComment(CommentMapper.toCommentDto(comment), item.getId(), user1.getId()))
                .isInstanceOf(NotAvailableException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

    @Test
    void whenAddCommentByInvalidItemIdIsNotSuccess() {
        String expectedMessage = "Вещь " + 999L + " не найдена";
        Comment comment = Comment.builder()
                .created(LocalDateTime.now())
                .author(user1)
                .text("text")
                .id(1L)
                .build();
        when(bookingRepository.existsBookingByItemIdAndBookerIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThatThrownBy(() -> itemService.addComment(CommentMapper.toCommentDto(comment), 999L, user1.getId()))
                .isInstanceOf(NotFoundException.class)
                .message()
                .isEqualTo(expectedMessage);
    }

}
