package ru.practicum.main.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.request.Request;
import ru.practicum.main.request.RequestStatus;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByEventId(Long eventId);

    Collection<Request> findAllByRequestId(Long requestId);

    Boolean existsByRequestIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, RequestStatus status);
}