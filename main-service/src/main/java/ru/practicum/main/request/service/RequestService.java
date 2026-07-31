package ru.practicum.main.request.service;

import ru.practicum.main.request.Request;
import ru.practicum.main.request.dto.UpdateRequestDto;
import ru.practicum.main.request.dto.UpdateRequestResultDto;

import java.util.Collection;

public interface RequestService {
    Request save(Long userId, Long eventId);

    Collection<Request> getAllByUserId(Long userId);

    Request updateCancel(Long userId, Long id);

    Collection<Request> getAllByUserIdAndEventId(Long userId, Long eventId);

    UpdateRequestResultDto updateAllByUserIdAndEventId(Long userId, Long eventId, UpdateRequestDto updateRequestDto);
}
