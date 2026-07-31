package ru.practicum.main.compilations.service;

import ru.practicum.main.compilations.Compilation;
import ru.practicum.main.compilations.dto.NewCompilationDto;
import ru.practicum.main.compilations.dto.UpdateCompilationDto;

import java.util.Collection;

public interface CompilationsService {
   Compilation save(NewCompilationDto newCompilationDto);

   void deleteById(Long id);

   Compilation updateCompilation(Long id, UpdateCompilationDto updateCompilationDto);

   Collection<Compilation> getAll(int from, int size, Boolean pinned);

   Compilation getById(Long id);
}
