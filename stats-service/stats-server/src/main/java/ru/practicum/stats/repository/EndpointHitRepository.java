package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.EndpointHit;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("select new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, count(eh.id)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.id) desc")
    Collection<ViewStatsDto> findStatsByPeriod(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, count(eh.id)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "and eh.uri in ?3 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.id) desc")
    Collection<ViewStatsDto> findStatsByUriAndPeriod(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("select new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.ip) desc")
    Collection<ViewStatsDto> findStatsByPeriodAndUniqueTrue(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stats.dto.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpointHit eh " +
            "where eh.timestamp between ?1 and ?2 " +
            "and eh.uri in ?3 " +
            "group by eh.app, eh.uri " +
            "order by count(eh.ip) desc")
    Collection<ViewStatsDto> findStatsByUriAndPeriodAndUniqueTrue(LocalDateTime start, LocalDateTime end, List<String> uri);
}
