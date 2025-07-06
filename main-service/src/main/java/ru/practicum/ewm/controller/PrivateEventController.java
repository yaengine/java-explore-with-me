package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.ParticipationRequestService;
import ru.practicum.ewm.service.PrivateEventService;

import java.util.List;

/**
 * Контроллер для работы с событиями текущего пользователя.
 * Предоставляет API для создания, обновления и просмотра событий,
 * а также управления заявками на участие в событиях.
 */
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
    private final PrivateEventService privateEventService;
    private final ParticipationRequestService participationRequestService;

    /**
     * Добавление нового события текущим пользователем
     *
     * @param userId идентификатор текущего пользователя
     * @param newEventDto DTO с данными нового события
     * @return DTO созданного события
     * @responseStatus 201 CREATED
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @RequestBody @Valid NewEventDto newEventDto) {
        return privateEventService.createEvent(userId, newEventDto);
    }

    /**
     * Обновляет событие текущего пользователя.
     *
     * @param userId идентификатор пользователя-владельца события
     * @param eventId идентификатор события для обновления
     * @param updateRequest DTO с обновляемыми данными события
     * @return полное DTO обновленного события
     * @responseStatus 200 OK
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateRequest) {
        return privateEventService.updateEvent(userId, eventId, updateRequest);
    }

    /**
     * Получение событий, добавленных текущим пользователем
     *
     * @param userId идентификатор текущего пользователя
     * @param from количество элементов, которые нужно пропустить (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 10)
     * @return список кратких DTO событий пользователя
     * @responseStatus 200 OK
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return privateEventService.getUserEvents(userId, from, size);
    }

    /**
     * Получение полной информации о событии добавленном текущим пользователем
     *
     * @param userId идентификатор текущего пользователя
     * @param eventId идентификатор запрашиваемого события
     * @return полное DTO запрошенного события
     * @responseStatus 200 OK
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        return privateEventService.getUserEventById(userId, eventId);
    }

    /**
     * Получение информации о запросах на участие в событии текущего пользователя
     *
     * @param userId идентификатор текущего пользователя
     * @param eventId идентификатор события
     * @return список DTO заявок на участие
     * @responseStatus 200 OK
     */
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        return participationRequestService.getEventRequests(userId, eventId);
    }

    /**
     * Изменение события добавленного текущим пользователем
     *
     * @param userId идентификатор текущего пользователя
     * @param eventId идентификатор события
     * @param request DTO с информацией об изменении статусов заявок
     * @return результат обновления статусов заявок
     * @see EventRequestStatusUpdateRequest
     * @see EventRequestStatusUpdateResult
     * @responseStatus 200 OK
     */
    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return participationRequestService.updateRequestStatus(userId, eventId, request);
    }
}
