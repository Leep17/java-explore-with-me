package ru.practicum.stats.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.EndpointHit;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.exception.BadDateException;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private  final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public EndpointHit save(EndpointHitDto endpointHitDto) {
        return endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public Collection<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (start == null || end == null) {
            throw new BadDateException("Ошибочный период статистики!");
        }

        if (start.isAfter(end)) {
            throw new BadDateException("Указаны ошибочные границы периода статистики!");
        }

        if (uris != null && !uris.isEmpty() && unique) {
             return endpointHitRepository.findStatsByUriAndPeriodAndUniqueTrue(start, end, uris);
        } else if (unique) {
            return endpointHitRepository.findStatsByPeriodAndUniqueTrue(start, end);
        } else if (uris != null && !uris.isEmpty()) {
            return endpointHitRepository.findStatsByUriAndPeriod(start, end, uris);
        }
        return endpointHitRepository.findStatsByPeriod(start, end);
    }
}
