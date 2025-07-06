package ru.practicum.ewm.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Публичный API контроллер для работы с событиями.
 * Предоставляет возможность поиска и просмотра опубликованных событий.
 */
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final PublicEventService publicEventService;

    /**
     * Получение событий с возможностью фильтрации
     *
     * @param text текст для поиска в содержимом аннотации и подробном описании события
     * @param categories список идентификаторов категорий для фильтрации
     * @param paid поиск только платных/бесплатных событий
     * @param rangeStart дата и время не раньше которых должно произойти событие (формат yyyy-MM-dd HH:mm:ss)
     * @param rangeEnd дата и время не позже которых должно произойти событие (формат yyyy-MM-dd HH:mm:ss)
     * @param onlyAvailable только события у которых не исчерпан лимит запросов на участие
     * @param sort только события у которых не исчерпан лимит запросов на участие
     * @param from количество событий, которые нужно пропустить для формирования текущего набора (по умолчанию 0)
     * @param size количество событий в наборе (по умолчанию 10)
     * @param request объект HTTP запроса для учета статистики просмотров
     * @return список DTO событий, удовлетворяющих условиям фильтрации
     */
    @GetMapping
    public List<EventShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) EventSort sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        return publicEventService.getPublishedEvents(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    /**
     * Получение подробной информации об опубликованном событии по его идентификатору
     * Учитывает просмотр события в статистике.
     *
     * @param id идентификатор события
     * @param request объект HTTP запроса для учета статистики просмотров
     * @return полное DTO запрошенного события
     */
    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        return publicEventService.getPublishedEventById(id, request);
    }
}
