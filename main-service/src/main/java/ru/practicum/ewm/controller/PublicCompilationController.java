package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

/**
 * Публичный API контроллер для работы с подборками событий.
 * Предоставляет доступ к информации о подборках событий для всех пользователей.
 */
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    /**
     * Получение подборок событий
     *
     * @param pinned флаг закрепленности подборки (true - закрепленные, false - незакрепленные, null - все)
     * @param from количество элементов, которые нужно пропустить (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 10)
     * @return список DTO подборок событий
     * @responseStatus 200 OK
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return compilationService.getCompilations(pinned, from, size);
    }

    /**
     * Получение подборки событий по её идентификатору.
     *
     * @param compId идентификатор подборки событий
     * @return DTO запрашиваемой подборки событий
     * @responseStatus 200 OK
     */
    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilation(@PathVariable Long compId) {
        return compilationService.getCompilationById(compId);
    }
}
