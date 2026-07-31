package ru.practicum.main.compilations.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.compilations.Compilation;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationDto;
import ru.practicum.main.compilations.repository.CompilationRepository;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.exception.NotFoundException;

import java.util.Collection;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public Compilation save(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(
                newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false);
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents())));
        } else {
            compilation.setEvents(new HashSet<>());
        }
        return compilationRepository.save(compilation);
    }

    @Override
    public void deleteById(Long id) {
        compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + id + " не найдена"));
        compilationRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Compilation updateCompilation(Long id, UpdateCompilationDto updateCompilationDto) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + id + " не найдена"));
        if (updateCompilationDto.getTitle() != null) {
            compilation.setTitle(updateCompilationDto.getTitle());
        }
        if (updateCompilationDto.getPinned() != null) {
            compilation.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getEvents() != null) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(updateCompilationDto.getEvents())));
        }
        return compilation;
    }

    @Override
    public Collection<Compilation> getAll(int from, int size, Boolean pinned) {
        return compilationRepository.findAll().stream()
                .filter(compilation -> pinned == null || compilation.isPinned() == pinned)
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public Compilation getById(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + id + " не найдена"));
    }
}
