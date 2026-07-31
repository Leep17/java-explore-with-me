package ru.practicum.main.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.service.CategoryService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    public Collection<CategoryDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                                @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAll(from, size).stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .toList();
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getById(@PathVariable Long catId) {
        Category category = categoryService.getById(catId);
        return new CategoryDto(category.getId(), category.getName());
    }

    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto saveNewCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        Category category = categoryService.save(newCategoryDto);
        return new CategoryDto(category.getId(), category.getName());
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        categoryService.deleteById(catId);
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody NewCategoryDto newCategoryDto) {
        Category category = categoryService.updateCategory(catId, newCategoryDto);
        return new CategoryDto(category.getId(), category.getName());
    }
}
