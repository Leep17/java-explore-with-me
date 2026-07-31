package ru.practicum.main.request.mapper;

import ru.practicum.main.request.Request;
import ru.practicum.main.request.dto.RequestDto;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequest().getId(),
                request.getStatus());
    }
}
