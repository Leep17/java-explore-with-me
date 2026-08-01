package ru.practicum.main.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.dto.EndpointHitDto;
import ru.practicum.main.category.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.event.Event;
import ru.practicum.main.event.EventState;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.BadRequestException;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.User;
import ru.practicum.main.user.repository.UserRepository;
import ru.practicum.client.EndpointHitClient;
import ru.practicum.client.dto.ViewStatsDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final EndpointHitClient endpointHitClient;
    private final ObjectMapper objectMapper;
    @Value("${spring.application.name}")
    private String applicationName;

    @Transactional
    @Override
    public Event save(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id=" + newEventDto.getCategory() + " не найдена"));

        if (newEventDto.getEventDate()
                .isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата события должна быть не раньше чем через 2 часа");
        }

        return eventRepository.save(EventMapper.toEvent(newEventDto, user, category));
    }

    @Override
    public Collection<Event> getAll(Long userId, int from, int size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return eventRepository.findAllByInitiatorId(userId).stream()
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public Event getByIdAndUserId(Long userId, Long id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return eventRepository.findByInitiatorIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));
    }

    @Transactional
    @Override
    public Event update(Long userId, Long id, UpdateEventDto updateEventDto) {

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findByInitiatorIdAndId(userId, id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Опубликованное событие нельзя изменить");
        }

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + updateEventDto.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("Дата события должна быть не раньше чем через 2 часа");
            }
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getLocation() != null) {
            if (updateEventDto.getLocation().getLon() != null) {
                event.setLon(updateEventDto.getLocation().getLon());
            }
            if (updateEventDto.getLocation().getLat() != null) {
                event.setLat(updateEventDto.getLocation().getLat());
            }
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getStateAction() == StateAction.SEND_TO_REVIEW) {
            event.setState(EventState.PENDING);
        }
        if (updateEventDto.getStateAction() == StateAction.CANCEL_REVIEW) {
            event.setState(EventState.CANCELED);
        }
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        return event;
    }

    @Override
    public Event getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие с id=" + id + " не найдено");
        }
        return event;
    }

    @Override
    public Collection<Event> getAll(String text, Set<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, int from, int size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата начала диапазона не может быть позже даты окончания");
        }

        LocalDateTime actualRangeStart = rangeStart != null ? rangeStart : LocalDateTime.now();

        String searchText = text != null ? text.toLowerCase() : null;

        List<Event> events;

        if (categories == null || categories.isEmpty()) {
            events = eventRepository.findPublicEvents(
                    text,
                    paid,
                    actualRangeStart,
                    rangeEnd,
                    onlyAvailable);
        } else {
            events = eventRepository.findPublicEventsByCategories(
                    text,
                    categories,
                    paid,
                    actualRangeStart,
                    rangeEnd,
                    onlyAvailable
            );
        }

        if (sort == EventSort.EVENT_DATE) {
            events = events.stream()
                    .sorted(Comparator.comparing(Event::getEventDate))
                    .toList();
        }

        if (sort == EventSort.VIEWS && !events.isEmpty()) {
            List<String> uris = events.stream()
                    .map(event -> "/events/" + event.getId())
                    .toList();

            LocalDateTime statsStart = events.stream()
                    .map(Event::getCreatedOn)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());

            ResponseEntity<Object> response =
                    endpointHitClient.getStatsByUriAndPeriodAndUniqueTrue(
                            statsStart,
                            LocalDateTime.now(),
                            uris
                    );
            List<ViewStatsDto> stats;

            if (response.getBody() == null) {
                stats = List.of();
            } else {
                stats = objectMapper.convertValue(
                        response.getBody(),
                        new TypeReference<List<ViewStatsDto>>() {
                        }
                );
            }
            Map<String, Long> viewsByUri = stats.stream()
                        .collect(Collectors.toMap(
                                ViewStatsDto::getUri,
                                ViewStatsDto::getHits,
                                Long::sum));

            events = events.stream()
                    .sorted(Comparator.comparingLong((Event event) -> viewsByUri.getOrDefault("/events/" + event.getId(), 0L)
                            ).reversed())
                    .toList();

        }
        return events.stream()
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public Collection<Event> getAllAdmin(Set<Long> users, Set<EventState> states, Set<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата начала диапазона не может быть позже даты окончания");
        }
        boolean filterUsers = users != null && !users.isEmpty();
        boolean filterStates = states != null && !states.isEmpty();
        boolean filterCategories = categories != null && !categories.isEmpty();

        Set<Long> usersForQuery = filterUsers ? users : Set.of(-1L);

        Set<EventState> statesForQuery = filterStates ? states : Set.of(EventState.PENDING);

        Set<Long> categoriesForQuery = filterCategories ? categories : Set.of(-1L);

        return eventRepository.findAdminEvents(
                        filterUsers,
                        usersForQuery,
                        filterStates,
                        statesForQuery,
                        filterCategories,
                        categoriesForQuery,
                        rangeStart,
                        rangeEnd).stream()
                .skip(from)
                .limit(size)
                .toList();
    }

    @Transactional
    @Override
    public Event updateById(Long id, UpdateEventAdminDto updateEventAdminDto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + id + " не найдено"));
        if (updateEventAdminDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminDto.getAnnotation());
        }
        if (updateEventAdminDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + updateEventAdminDto.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (updateEventAdminDto.getDescription() != null) {
            event.setDescription(updateEventAdminDto.getDescription());
        }
        if (updateEventAdminDto.getEventDate() != null) {
            if (updateEventAdminDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("Дата события должна быть не раньше чем через 1 час");
            }
            event.setEventDate(updateEventAdminDto.getEventDate());
        }
        if (updateEventAdminDto.getLocation() != null) {
            if (updateEventAdminDto.getLocation().getLon() != null) {
                event.setLon(updateEventAdminDto.getLocation().getLon());
            }
            if (updateEventAdminDto.getLocation().getLat() != null) {
                event.setLat(updateEventAdminDto.getLocation().getLat());
            }
        }
        if (updateEventAdminDto.getPaid() != null) {
            event.setPaid(updateEventAdminDto.getPaid());
        }
        if (updateEventAdminDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminDto.getParticipantLimit());
        }
        if (updateEventAdminDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminDto.getRequestModeration());
        }
        if (updateEventAdminDto.getTitle() != null) {
            event.setTitle(updateEventAdminDto.getTitle());
        }
        if (updateEventAdminDto.getStateActionAdmin() == StateActionAdmin.PUBLISH_EVENT) {

            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Опубликовать можно только событие в состоянии PENDING");
            }

            LocalDateTime publicationTime = LocalDateTime.now();

            if (event.getEventDate()
                    .isBefore(publicationTime.plusHours(1))) {
                throw new ConflictException("Дата события должна быть не раньше чем через 1 час после публикации");
            }

            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(publicationTime);
        }

        if (updateEventAdminDto.getStateActionAdmin() == StateActionAdmin.REJECT_EVENT) {

            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Нельзя отклонить уже опубликованное событие");
            }

            event.setState(EventState.CANCELED);
        }
        return event;
    }

    @Override
    public long getConfirmedRequests(Long eventId) {
        return requestRepository.countByEventIdAndStatus(
                eventId,
                RequestStatus.CONFIRMED
        );
    }

    @Override
    public long getViews(Event event) {
        ResponseEntity<Object> response = endpointHitClient.getStatsByUriAndPeriodAndUniqueTrue(
                        event.getCreatedOn(),
                        LocalDateTime.now(),
                        List.of("/events/" + event.getId())
                );

        if (response.getBody() == null) {
            return 0L;
        }

        List<ViewStatsDto> stats = objectMapper.convertValue(
                response.getBody(),
                new TypeReference<List<ViewStatsDto>>() {
                }
        );

        return stats.stream()
                .mapToLong(ViewStatsDto::getHits)
                .sum();
    }

    @Override
    public void saveHit(String uri, String ip) {
        endpointHitClient.saveEndpointHit(new EndpointHitDto(
                        applicationName,
                        uri,
                        ip,
                        LocalDateTime.now()));
    }
}
