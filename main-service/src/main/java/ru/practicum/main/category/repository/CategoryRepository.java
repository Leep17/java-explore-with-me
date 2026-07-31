package ru.practicum.main.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
