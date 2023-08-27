package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> jsonCommentDto;

    @Test
    @SneakyThrows
    void commentDtoTest() {
        CommentDto commentDto = CommentDto.builder()
                .created(LocalDateTime.now())
                .build();

        JsonContent<CommentDto> result = jsonCommentDto.write(commentDto);

        assertThat(result).hasJsonPath("$.created");
    }

}
