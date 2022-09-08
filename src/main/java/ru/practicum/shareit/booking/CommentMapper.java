package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CommentDto;
import ru.practicum.shareit.booking.model.Comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .createdAt(commentDto.getCreatedAt())
                .build();
    }
}