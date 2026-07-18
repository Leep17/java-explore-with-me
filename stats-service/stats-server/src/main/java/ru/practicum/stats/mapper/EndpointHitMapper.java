package ru.practicum.stats.mapper;

import ru.practicum.stats.EndpointHit;
import ru.practicum.stats.dto.EndpointHitDto;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        return new EndpointHit(endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp());
    }
}
