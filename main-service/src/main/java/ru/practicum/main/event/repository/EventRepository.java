package ru.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.event.Event;
import java.util.Collection;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Collection<Event> findAllByInitiatorId(Long userId);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);
}
