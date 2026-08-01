package ru.practicum.main.event.service;

import ru.practicum.main.event.Event;
import ru.practicum.main.event.EventState;
import ru.practicum.main.event.dto.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EventService {
    Event save(Long userId, NewEventDto newEventDto);

    Collection<Event> getAll(Long userId, int from, int size);

    Event getByIdAndUserId(Long userId, Long id);

    Event update(Long userId, Long id, UpdateEventDto updateEventDto);

    Event getById(Long id);

    Collection<Event> getAll(String text, Set<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size);

    Collection<Event> getAllAdmin(Set<Long> users, Set<EventState> states, Set<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    Event updateById(Long id, UpdateEventAdminDto updateEventAdminDto);

    long getConfirmedRequests(Long eventId);

    long getViews(Event event);

    void saveHit(String uri, String ip);
}
