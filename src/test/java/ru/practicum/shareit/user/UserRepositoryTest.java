package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@DataJpaTest
public class UserRepositoryTest {

    private final TestEntityManager entityManager;
    private final UserRepository userRepository;

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Test
    void whenExistsUserByEmail() {
        User user = User.builder()
                .name("name")
                .email("test@test.ru")
                .build();
        entityManager.persist(user);

        Boolean actual = userRepository.existsUserByEmail("test@test.ru");

        assertThat(actual).isTrue();
    }
}
