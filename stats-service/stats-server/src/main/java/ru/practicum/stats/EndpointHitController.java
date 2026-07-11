package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.service.EndpointHitService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EndpointHitController {

    private final EndpointHitService endpointHitService;

    @GetMapping("/stats")
    public Collection<ViewStatsDto> getStatistic(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime start,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime end,
                                                 @RequestParam(required = false) List<String> uris,
                                                 @RequestParam(defaultValue = "false") boolean unique
                                                 ) {
        if (uris == null) {
            uris = List.of();
        }

        return endpointHitService.getStats(start, end, uris, unique);

    }

    @PostMapping("/hit")
    public EndpointHit postEndpointHit(@RequestBody EndpointHitDto endpointHitDto) {

        return endpointHitService.save(endpointHitDto);

    }

}
