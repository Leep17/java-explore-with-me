package ru.practicum.main.category.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.main.category.Category;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public Category save(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с названием " + newCategoryDto.getName() + " уже существует");
        }
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не найдена"));
        categoryRepository.deleteById(id);
    }

    @Override
    public Collection<Category> getAll(int from, int size) {
        return categoryRepository.findAll().stream()
                .skip(from)
                .limit(size)
                .toList();
    }

    @Override
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не найдена"));
    }

    @Transactional
    @Override
    public Category updateCategory(Long id, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + id + " не найдена"));
        if (categoryRepository.existsByNameAndIdNot(newCategoryDto.getName(), id)) {
            throw new ConflictException("Категория с названием " + newCategoryDto.getName() + " уже существует");
        }
        category.setName(newCategoryDto.getName());
        return category;
    }
}
