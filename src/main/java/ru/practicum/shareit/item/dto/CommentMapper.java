package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(Long userId, CommentDto commentDto, Long itemId) {
        return new Comment(
                commentDto.getText(),
                itemId,
                userId);
    }

    public static CommentOutDto toCommentOutDto(Comment comment) {
        return new CommentOutDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthorId(),
                comment.getCreated());
    }

    public static List<CommentOutDto> toCommentOutDtoList(List<Comment> comments) {
        List<CommentOutDto> result = new ArrayList<>();
        for (Comment comment : comments) {
            result.add(toCommentOutDto(comment));
        }
        return result;
    }

}
