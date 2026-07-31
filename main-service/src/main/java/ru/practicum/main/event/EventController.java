package ru.practicum.main.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.EndpointHitClient;
import ru.practicum.client.dto.EndpointHitDto;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final EndpointHitClient endpointHitClient;

    @GetMapping("/events")
    public Collection<EventShortDto> getAll(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) Set<Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(required = false) EventSort sort,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size,
                                            HttpServletRequest request) {

        Collection<Event> events = eventService.getAll(
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                from,
                size
        );

        endpointHitClient.saveEndpointHit(new EndpointHitDto("ewm-main-service",
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LocalDateTime.now())
        );

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @GetMapping("/events/{id}")
    public EventFullDto getById(@PathVariable Long id,
                                HttpServletRequest request) {

        Event event = eventService.getById(id);

        endpointHitClient.saveEndpointHit(new EndpointHitDto("ewm-main-service",
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        LocalDateTime.now()
                )
        );

        return eventMapper.toEventFullDto(event);
    }

    @GetMapping("/users/{userId}/events")
    public Collection<EventShortDto> getAll(@PathVariable Long userId,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size) {

        return eventService.getAll(userId, from, size).stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getByUserIdAndId(@PathVariable Long userId,
                                         @PathVariable Long eventId) {

        return eventMapper.toEventFullDto(eventService.getByIdAndUserId(userId,eventId));
    }

    @GetMapping("/admin/events")
    public Collection<EventFullDto> getAdminAll(@RequestParam(required = false) Set<Long> users,
                                                 @RequestParam(required = false) Set<EventState> states,
                                                 @RequestParam(required = false) Set<Long> categories,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {

        return eventService.getAllAdmin(users, states, categories, rangeStart, rangeEnd, from, size).stream()
                .map(eventMapper::toEventFullDto)
                .toList();

    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvents(@PathVariable Long userId,
                                   @RequestBody NewEventDto newEventDto) {
        return eventMapper.toEventFullDto(eventService.save(userId, newEventDto));
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateByUserIdAndEventId(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 @RequestBody UpdateEventDto updateEventDto) {

        return eventMapper.toEventFullDto(eventService.update(userId, eventId, updateEventDto));
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateAdmin(@PathVariable Long eventId,
                                    @RequestBody UpdateEventAdminDto updateEventAdminDto) {
        return eventMapper.toEventFullDto(eventService.updateById(eventId, updateEventAdminDto));
    }

}
