package ru.practicum.main.comment.service;

import ru.practicum.main.comment.Comment;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;

import java.util.Collection;

public interface CommentService {
    Comment save(Long authorId, Long eventId, NewCommentDto newCommentDto);

    Comment update(Long authorId, Long commentId, UpdateCommentDto updateCommentDto);

    void deleteById(Long authorId, Long commentId);

    Comment findById(Long commentId);

    Collection<Comment> findByAuthor(Long authorId);

    Collection<Comment> findByEvent(Long eventId);

    Collection<Comment> findByAuthorAndEvent(Long authorId, Long eventId);
}
