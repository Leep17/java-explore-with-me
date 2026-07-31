package ru.practicum.main.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.request.dto.RequestDto;
import ru.practicum.main.request.dto.UpdateRequestDto;
import ru.practicum.main.request.dto.UpdateRequestResultDto;
import ru.practicum.main.request.mapper.RequestMapper;
import ru.practicum.main.request.service.RequestService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public Collection<RequestDto> getAll(@PathVariable Long userId) {
        return requestService.getAllByUserId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public Collection<RequestDto> getAllByUserIdAndEventId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {

        return requestService.getAllByUserIdAndEventId(userId, eventId).stream()
                .map(RequestMapper::toRequestDto)
                .toList();
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto saveRequest(@PathVariable Long userId,
                                   @RequestParam Long eventId) {

        return RequestMapper.toRequestDto(requestService.save(userId, eventId));
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public UpdateRequestResultDto updateWithUserIdAndEventId(@PathVariable Long userId,
                                                             @PathVariable Long eventId,
                                                             @RequestBody UpdateRequestDto updateRequestDto) {

        return requestService.updateAllByUserIdAndEventId(userId, eventId, updateRequestDto);
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public RequestDto updateCancel(@PathVariable Long userId,
                                   @PathVariable Long requestId) {
        return RequestMapper.toRequestDto(requestService.updateCancel(userId, requestId));
    }
}
