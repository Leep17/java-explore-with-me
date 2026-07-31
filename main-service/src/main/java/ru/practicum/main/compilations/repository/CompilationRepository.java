package ru.practicum.main.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.compilations.Compilation;

import java.util.Collection;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Collection<Compilation> findAllByPinned(Boolean pinned);
}
