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

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CompilationController {
    private final CompilationsService compilationsService;
    private final CompilationMapper compilationMapper;

    @GetMapping("/compilations")
    public Collection<CompilationDto> getAll(@RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size,
                                             @RequestParam(required = false) Boolean pinned) {
      return compilationsService.getAll(from, size, pinned).stream()
              .map(compilationMapper::toCompilationDto)
              .toList();
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        return compilationMapper.toCompilationDto(compilationsService.getById(compId));
    }

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveNewCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationMapper.toCompilationDto(compilationsService.save(newCompilationDto));
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
        return compilationMapper.toCompilationDto(compilation);
    }

}
