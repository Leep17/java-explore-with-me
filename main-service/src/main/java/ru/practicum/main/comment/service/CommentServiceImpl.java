package ru.practicum.main.comment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.comment.Comment;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.repository.CommentRepository;
import ru.practicum.main.event.Event;
import ru.practicum.main.event.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.user.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public Comment save(Long authorId, Long eventId, NewCommentDto newCommentDto) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + authorId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя добавить комментарий к неопубликованному событию.");
        }

        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    @Transactional
    @Override
    public Comment update(Long authorId, Long commentId, UpdateCommentDto updateCommentDto) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + authorId + " не найден"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + commentId + " не найден"));

        if (!authorId.equals(comment.getAuthor().getId())) {
            throw new ConflictException("Пользователь не является автором комментария");

        }

        comment.setText(updateCommentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return comment;
    }

    @Transactional
    @Override
    public void deleteById(Long authorId, Long commentId) {

        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + authorId + " не найден"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + commentId + " не найден"));

        if (!authorId.equals(comment.getAuthor().getId())) {
            throw new ConflictException("Пользователь не является автором комментария");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment findById(Long commentId) {


        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id=" + commentId + " не найден"));

        if (comment.getEvent().getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя получить комментарии неопубликованного события.");
        }

        return comment;
    }

    @Override
    public Collection<Comment> findByAuthor(Long authorId) {

        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + authorId + " не найден"));

        return commentRepository.findAllByAuthorIdOrderByCreatedOnDesc(authorId).stream()
                .filter(comment -> comment.getEvent().getState() == EventState.PUBLISHED)
                .toList();
    }

    @Override
    public Collection<Comment> findByEvent(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя получить комментарии неопубликованного события.");
        }

        return commentRepository.findAllByEventIdOrderByCreatedOnDesc(eventId);
    }

    @Override
    public Collection<Comment> findByAuthorAndEvent(Long authorId, Long eventId) {

        userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + authorId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя получить комментарии неопубликованного события.");
        }

        return commentRepository.findAllByAuthorIdAndEventIdOrderByCreatedOnDesc(authorId, eventId);
    }
}
