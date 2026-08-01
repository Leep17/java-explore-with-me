package ru.practicum.main.compilations;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationDto;
import ru.practicum.main.compilations.mapper.CompilationMapper;
import ru.practicum.main.compilations.service.CompilationsService;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.mapper.EventMapper;
import ru.practicum.main.event.service.EventService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationsService compilationsService;
    private final EventService eventService;

    @GetMapping("/compilations")
    public Collection<CompilationDto> getAll(@RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) Boolean pinned) {


      return compilationsService.getAll(from, size, pinned).stream()
              .map(compilation -> CompilationMapper.toCompilationDto(compilation,
                      compilation.getEvents().stream()
                              .map(event -> EventMapper.toEventShortDto(
                                      event,
                                      eventService.getConfirmedRequests(event.getId()),
                                      eventService.getViews(event)
                              ))
                              .toList()))
              .toList();
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {

        Compilation compilation = compilationsService.getById(compId);
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        eventService.getConfirmedRequests(event.getId()),
                        eventService.getViews(event)
                ))
                .toList();
        return CompilationMapper.toCompilationDto(compilation, events);
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {

        Compilation compilation = compilationsService.save(newCompilationDto);
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        eventService.getConfirmedRequests(event.getId()),
                        eventService.getViews(event)
                ))
                .toList();
        return CompilationMapper.toCompilationDto(compilation, events);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long compId) {
        compilationsService.deleteById(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationsService.updateCompilation(compId, updateCompilationDto);
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(
                        event,
                        eventService.getConfirmedRequests(event.getId()),
                        eventService.getViews(event)
                ))
                .toList();
        return CompilationMapper.toCompilationDto(compilation, events);
    }

}
