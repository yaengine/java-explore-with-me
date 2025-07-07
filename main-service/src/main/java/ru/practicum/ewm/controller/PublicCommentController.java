package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.service.PublicCommentService;

import java.util.List;

/**
 * Публичный API для работы с комментариями.
 * Предоставляет методы для получения комментариев
 */
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class PublicCommentController {
    private final PublicCommentService publicCommentService;

    /**
     * Получает список всех комментариев для указанного события.
     *
     * @param eventId идентификатор события, для которого запрашиваются комментарии
     * @return список DTO комментариев, относящихся к указанному событию
     */
    @GetMapping("/events/{eventId}")
    public List<CommentDto> getEventComments(@PathVariable Long eventId) {
        return publicCommentService.getEventComments(eventId);
    }

    /**
     * Получает комментарий по его идентификатору.
     *
     * @param commentId идентификатор запрашиваемого комментария
     * @return DTO комментария с указанным идентификатором
     */
    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable Long commentId) {
        return publicCommentService.getComment(commentId);
    }

}
