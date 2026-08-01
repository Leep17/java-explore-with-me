package ru.practicum.main.compilations.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.main.compilations.Compilation;
import ru.practicum.main.compilations.dto.CompilationDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.mapper.EventMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {

        return new CompilationDto(
                compilation.getId(),
                events,
                compilation.isPinned(),
                compilation.getTitle()
        );
    }
}