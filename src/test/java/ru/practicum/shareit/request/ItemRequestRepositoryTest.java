package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class ItemRequestRepositoryTest {

    private final TestEntityManager entityManager;
    private final ItemRequestRepository itemRequestRepository;

    private User user1;
    private User user2;
    private ItemRequest itemRequest1;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("name1")
                .email("test@test.ru")
                .build();

        user2 = User.builder()
                .name("name2")
                .email("test1@test.ru")
                .build();

        itemRequest1 = ItemRequest.builder()
                .requester(user1)
                .created(LocalDateTime.now().plusDays(1))
                .description("desc1")
                .build();

        itemRequest2 = ItemRequest.builder()
                .requester(user1)
                .created(LocalDateTime.now())
                .description("desc2")
                .build();

        itemRequest3 = ItemRequest.builder()
                .requester(user2)
                .created(LocalDateTime.now())
                .description("desc2")
                .build();
    }

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Test
    void whenFindAllByRequesterIdOrderByCreatedDesc() {
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(itemRequest1);
        entityManager.persist(itemRequest2);
        entityManager.persist(itemRequest3);
        List<ItemRequest> expected = List.of(itemRequest1, itemRequest2);

        List<ItemRequest> actual = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user1.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindByIdIsNotOrderByCreatedAsc() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(itemRequest1);
        entityManager.persist(itemRequest2);
        Page<ItemRequest> expected = new PageImpl<>(List.of(itemRequest2));

        Page<ItemRequest> actual = itemRequestRepository.findByIdIsNotOrderByCreatedAsc(1L, pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
