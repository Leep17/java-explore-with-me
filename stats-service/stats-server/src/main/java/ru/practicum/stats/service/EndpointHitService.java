package ru.practicum.stats.service;

import ru.practicum.stats.EndpointHit;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointHitService {
    EndpointHit save(EndpointHitDto endpointHitDto);

    Collection<ViewStatsDto> getStats(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      boolean unique);
}
