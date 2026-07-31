package ru.practicum.main.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    private Long id;
    private Collection<EventShortDto> events;
    private Boolean pinned;
    private String title;
}