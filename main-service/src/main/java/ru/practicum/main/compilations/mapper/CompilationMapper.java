package ru.practicum.main.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.compilations.Compilation;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.event.mapper.EventMapper;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public CompilationDto toCompilationDto(Compilation compilation) {

        return new CompilationDto(
                compilation.getId(),
                compilation.getEvents().stream()
                        .map(eventMapper::toEventShortDto)
                        .toList(),
                compilation.isPinned(),
                compilation.getTitle()
        );
    }
}