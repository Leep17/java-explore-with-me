package ru.practicum.main.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.comment.dto.CommentDto;
import ru.practicum.main.comment.dto.NewCommentDto;
import ru.practicum.main.comment.dto.UpdateCommentDto;
import ru.practicum.main.comment.mapper.CommentMapper;
import ru.practicum.main.comment.service.CommentService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/comments/{id}")
    public CommentDto getById(@PathVariable Long id) {
        return CommentMapper.toCommentDto(commentService.findById(id));
    }

    @GetMapping("/events/{eventId}/comments")
    public Collection<CommentDto> getByEventId(@PathVariable Long eventId) {
        return commentService.findByEvent(eventId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @GetMapping("/users/{userId}/comments")
    public Collection<CommentDto> getByAuthorId(@PathVariable Long userId) {
        return commentService.findByAuthor(userId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @GetMapping("/users/{userId}/events/{eventId}/comments")
    public Collection<CommentDto> getByUserIdAndEventId(@PathVariable Long userId,
                                                        @PathVariable Long eventId) {

        return commentService.findByAuthorAndEvent(userId, eventId).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @PostMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto saveNewComment(@PathVariable Long userId,
                                     @PathVariable Long eventId,
                                     @Valid @RequestBody NewCommentDto newCommentDto) {

        return CommentMapper.toCommentDto(commentService.save(userId, eventId, newCommentDto));
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long userId,
                           @PathVariable Long commentId) {

        commentService.deleteById(userId, commentId);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {

        return CommentMapper.toCommentDto(commentService.update(userId, commentId, updateCommentDto));
    }
}
