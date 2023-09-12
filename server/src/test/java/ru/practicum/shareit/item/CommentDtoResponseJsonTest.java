package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoResponseJsonTest {

    @Autowired
    private JacksonTester<CommentDtoResponse> jsonCommentDtoResponse;

    @Test
    @SneakyThrows
    void commentDtoResponseTest() {
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDtoResponse> result = jsonCommentDtoResponse.write(commentDtoResponse);

        assertThat(result).hasJsonPath("$.created");
    }

}
