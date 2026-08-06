package ru.practicum.main.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.comment.Comment;

import java.util.Collection;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByAuthorIdOrderByCreatedOnDesc(Long authorId);

    Collection<Comment> findAllByEventIdOrderByCreatedOnDesc(Long eventId);

    Collection<Comment> findAllByAuthorIdAndEventIdOrderByCreatedOnDesc(Long authorId, Long eventId);
}
