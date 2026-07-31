package ru.practicum.main.event.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.client.EndpointHitClient;
import ru.practicum.client.dto.ViewStatsDto;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.Event;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.request.RequestStatus;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final RequestRepository requestRepository;
    private final EndpointHitClient endpointHitClient;
    private final ObjectMapper objectMapper;

    public EventShortDto toEventShortDto(Event event) {

        long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        long views = getViews(event);

        return new EventShortDto(
                event.getAnnotation(),
                new CategoryDto(
                        event.getCategory().getId(),
                        event.getCategory().getName()
                ),
                confirmedRequests,
                event.getEventDate(),
                event.getId(),
                new UserShortDto(
                        event.getInitiator().getId(),
                        event.getInitiator().getName()
                ),
                event.isPaid(),
                event.getTitle(),
                views
        );
    }

    public EventFullDto toEventFullDto(Event event) {

        long confirmedRequests = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);

        long views = getViews(event);

        return new EventFullDto(event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                confirmedRequests,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                new LocationDto(event.getLat(), event.getLon()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                views);
    }


    private long getViews(Event event) {
        String uri = "/events/" + event.getId();

        ResponseEntity<Object> response =
                endpointHitClient.getStatsByUriAndPeriodAndUniqueTrue(
                        event.getCreatedOn(),
                        LocalDateTime.now(),
                        List.of(uri)
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
                .filter(stat -> uri.equals(stat.getUri()))
                .mapToLong(ViewStatsDto::getHits)
                .sum();
    }
}
