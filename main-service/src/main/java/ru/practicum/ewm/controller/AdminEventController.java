package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для административных операций с событиями.
 * Предоставляет API для поиска и редактирования событий администратором.
 */
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {
    private final AdminEventService adminEventService;

    /**
     * Поиск событий администратором
     *
     * @param users список идентификаторов пользователей (опционально)
     * @param states список состояний событий для фильтрации (опционально)
     * @param categories список идентификаторов категорий для фильтрации (опционально)
     * @param rangeStart дата и время начала периода по дате события (формат yyyy-MM-dd HH:mm:ss)
     * @param rangeEnd дата и время окончания периода по дате события (формат yyyy-MM-dd HH:mm:ss)
     * @param from количество событий, которые нужно пропустить (по умолчанию 0)
     * @param size количество событий в наборе (по умолчанию 10)
     * @return список DTO событий, удовлетворяющих условиям фильтрации
     */
    @GetMapping
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return adminEventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    /**
     * Редактирование события администратором
     *
     * @param eventId идентификатор события для обновления
     * @param updateRequest DTO с обновляемыми данными события
     * @return DTO обновленного события
     */
    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateRequest) {
        return adminEventService.updateEvent(eventId, updateRequest);
    }
}
