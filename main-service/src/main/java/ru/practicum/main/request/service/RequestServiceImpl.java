package ru.practicum.main.request.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.event.Event;
import ru.practicum.main.event.EventState;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;
import ru.practicum.main.request.Request;
import ru.practicum.main.request.RequestStatus;
import ru.practicum.main.request.dto.UpdateRequestDto;
import ru.practicum.main.request.dto.UpdateRequestResultDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.repository.RequestRepository;
import ru.practicum.main.user.User;
import ru.practicum.main.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public Request save(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Нельзя подать заявку на неопубликованное событие");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Пользователь не может подать заявку на своё событие");
        }

        if (requestRepository.existsByRequestIdAndEventId(userId, eventId)) {
            throw new ConflictException("Пользователь уже подал заявку на событие с id=" + eventId);
        }

        long confirmedRequests = requestRepository.countByEventIdAndStatus(
                eventId,
                RequestStatus.CONFIRMED
        );

        if (event.getParticipantLimit() > 0
                && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников события уже достигнут");
        }

        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);

        } else {
            request.setStatus(RequestStatus.PENDING);
        }
        request.setRequest(user);
        request.setEvent(event);
        return requestRepository.save(request);
    }

    @Override
    public Collection<Request> getAllByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return requestRepository.findAllByRequestId(userId);
    }

    @Transactional
    @Override
    public Request updateCancel(Long userId, Long id) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Request request = requestRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Запрос с id=" + id + " не найден"));

        if (!request.getRequest().getId().equals(userId)) {
            throw new NotFoundException("Запрос с id=" + id + " не принадлежит пользователю с id=" + userId);
        }

        request.setStatus(RequestStatus.CANCELED);

        return request;
    }

    @Override
    public Collection<Request> getAllByUserIdAndEventId(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " пользователя с id=" + userId + " не найдено"));

        return requestRepository.findAllByEventId(eventId);
    }

    @Transactional
    @Override
    public UpdateRequestResultDto updateAllByUserIdAndEventId(Long userId, Long eventId, UpdateRequestDto updateRequestDto) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " пользователя с id=" + userId + " не найдено"));


        Collection<Request> requests = requestRepository.findAllByEventId(eventId)
                .stream()
                .filter(request -> updateRequestDto.getRequestIds().contains(request.getId()))
                .toList();

        for (Request request : requests) {
             if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Статус можно менять только у заявок в состоянии ожидания");
             }
        }

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        if (updateRequestDto.getStatus() == RequestStatus.CONFIRMED && event.getParticipantLimit() > 0
                && confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников события уже достигнут");
        }

        if (updateRequestDto.getStatus() != RequestStatus.CONFIRMED && updateRequestDto.getStatus() != RequestStatus.REJECTED) {
            throw new ConflictException("Допустимы только статусы CONFIRMED и REJECTED");
        }

        if (updateRequestDto.getStatus() == RequestStatus.CONFIRMED && event.getParticipantLimit() > 0
                && confirmedRequests + requests.size() > event.getParticipantLimit()) {
            throw new ConflictException("Количество заявок превышает лимит участников");
        }

        requests.forEach(request -> request.setStatus(updateRequestDto.getStatus()));

        if (updateRequestDto.getStatus() == RequestStatus.CONFIRMED && event.getParticipantLimit() > 0
                && confirmedRequests + requests.size() == event.getParticipantLimit()) {

            requestRepository.findAllByEventId(eventId).stream()
                    .filter(request -> request.getStatus() == RequestStatus.PENDING)
                    .forEach(request -> request.setStatus(RequestStatus.REJECTED));
        }

        return new UpdateRequestResultDto(requests.stream().filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED)).map(RequestMapper::toRequestDto).toList(),
                requests.stream().filter(request -> request.getStatus().equals(RequestStatus.REJECTED)).map(RequestMapper::toRequestDto).toList());
    }
}
