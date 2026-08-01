package ru.practicum.main.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.main.category.Category;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.Event;
import ru.practicum.main.event.EventState;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.user.User;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;


@Component
public class EventMapper {

    public static EventShortDto toEventShortDto(Event event,
                                                long confirmedRequests,
                                                long views) {


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

    public static EventFullDto toEventFullDto(Event event,
                                              long confirmedRequests,
                                              long views) {

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

    public static Event toEvent(
            NewEventDto newEventDto,
            User user,
            Category category) {

        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLat(newEventDto.getLocation().getLat());
        event.setLon(newEventDto.getLocation().getLon());
        event.setPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false);
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true);
        event.setTitle(newEventDto.getTitle());
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        return event;
    }
}
