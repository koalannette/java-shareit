package ru.practicum.shareit.item.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
public class CommentDtoResponse {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}