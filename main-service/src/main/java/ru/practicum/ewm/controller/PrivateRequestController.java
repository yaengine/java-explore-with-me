package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

/**
 * Контроллер для управления запросами на участие в событиях.
 * Предоставляет API для создания, просмотра и отмены запросов текущего пользователя.
 */
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final ParticipationRequestService participationRequestService;

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     *
     * @param userId идентификатор текущего пользователя
     * @param eventId идентификатор события, для которого создается запрос
     * @return DTO созданного запроса на участие
     * @responseStatus 201 CREATED
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return participationRequestService.createRequest(userId, eventId);
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @param userId идентификатор текущего пользователя
     * @return список DTO запросов на участие
     * @responseStatus 200 OK
     */
    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return participationRequestService.getUserRequests(userId);
    }

    /**
     * Отмена своего запроса на участие в событии
     *
     * @param userId идентификатор текущего пользователя
     * @param requestId идентификатор запроса для отмены
     * @return DTO отмененного запроса
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return participationRequestService.cancelRequest(userId, requestId);
    }
}
