package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

/**
 * Публичный API контроллер для работы с категориями событий.
 * Предоставляет доступ к информации о категориях для всех пользователей.
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    /**
     * Получает список категорий с пагинацией.
     *
     * @param from количество элементов, которые нужно пропустить (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 10)
     * @return список DTO категорий
     * @responseStatus 200 OK
     */
    @GetMapping
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(from, size);
    }

    /**
     * Получение информации о категории по её идентификатору
     *
     * @param catId идентификатор категории
     * @return DTO запрашиваемой категории
     * @responseStatus 200 OK
     */
    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
