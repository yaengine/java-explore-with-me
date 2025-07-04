package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //Добавление запроса от текущего пользователя на участие в событии
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return participationRequestService.createRequest(userId, eventId);
    }

    @GetMapping
    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return participationRequestService.getUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    //Отмена своего запроса на участие в событии
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return participationRequestService.cancelRequest(userId, requestId);
    }
}
