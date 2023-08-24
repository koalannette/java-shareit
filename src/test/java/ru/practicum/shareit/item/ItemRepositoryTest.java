package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class ItemRepositoryTest {

    private final TestEntityManager entityManager;
    private final ItemRepository itemRepository;

    private final PageRequest pageRequest = PageRequest.of(0, 3);
    private User user;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("email@email.com")
                .name("name")
                .build();

        item1 = Item.builder()
                .name("firstName")
                .description("desc")
                .available(true)
                .owner(user)
                .build();

        item2 = Item.builder()
                .name("SecondName")
                .description("description")
                .available(true)
                .owner(user)
                .build();
    }

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Test
    void whenFindItemByOwnerId() {
        entityManager.persist(user);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> expected = List.of(item1, item2);

        List<Item> actual = itemRepository.findItemByOwner_Id(user.getId(), pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindItemByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue() {
        item2.setAvailable(false);
        entityManager.persist(user);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Item> expected = List.of(item1);

        List<Item> actual = itemRepository.findItemByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue("first", "desc", pageRequest);

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindAllItemIdByOwnerId() {
        entityManager.persist(user);
        entityManager.persist(item1);
        entityManager.persist(item2);
        List<Long> expected = List.of(item1.getId(), item2.getId());

        List<Long> actual = itemRepository.findAllItemIdByOwnerId(user.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenFindByRequestId() {
        entityManager.persist(user);
        ItemRequest itemRequest = ItemRequest.builder()
                .requester(user)
                .created(LocalDateTime.now())
                .description("desc")
                .build();
        item1.setRequest(itemRequest);
        entityManager.persist(itemRequest);
        entityManager.persist(item1);
        List<Item> expected = List.of(item1);

        List<Item> actual = itemRepository.findByRequest_Id(itemRequest.getId());

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void whenExistsItemByOwnerId() {
        entityManager.persist(user);
        entityManager.persist(item1);

        Boolean actual = itemRepository.existsItemByOwnerId(user.getId());

        assertThat(actual).isTrue();
    }
}
