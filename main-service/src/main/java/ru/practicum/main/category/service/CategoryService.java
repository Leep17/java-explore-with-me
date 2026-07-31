package ru.practicum.main.category.service;

import ru.practicum.main.category.Category;
import ru.practicum.main.category.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
     Category save(NewCategoryDto newCategoryDto);

     void deleteById(Long id);

     Collection<Category> getAll(int from, int size);

     Category getById(Long id);

     Category updateCategory(Long id, NewCategoryDto newCategoryDto);
}
