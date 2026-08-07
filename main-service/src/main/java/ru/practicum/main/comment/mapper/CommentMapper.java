package ru.practicum.main.comment.mapper;

import ru.practicum.main.comment.Comment;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.user.mapper.UserMapper;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getText(),
                UserMapper.toUserShortDto(comment.getAuthor()),
                comment.getEvent().getId(),
                comment.getCreatedOn(),
                comment.getUpdatedOn());
    }
}
