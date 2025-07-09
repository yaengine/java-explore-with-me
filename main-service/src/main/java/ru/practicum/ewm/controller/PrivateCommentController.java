package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.service.PrivateCommentService;

/**
 * Контроллер для работы с комментариями текущего пользователя.
 * Предоставляет API для создания, просмотра, обновления и удаления комментариев пользователями.
 */
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final PrivateCommentService privateCommentService;

    /**
     * Создает новый комментарий к событию.
     *
     * @param userId идентификатор пользователя, создающего комментарий
     * @param eventId идентификатор события, к которому относится комментарий
     * @param newCommentDto DTO с данными нового комментария
     * @return созданный комментарий в формате DTO
     */
    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return privateCommentService.addComment(userId, eventId, newCommentDto);
    }

    /**
     * Получает комментарий по его идентификатору.
     *
     * @param userId идентификатор пользователя, автора комментария
     * @param commentId идентификатор запрашиваемого комментария
     * @return DTO запрошенного комментария
     */
    @GetMapping("{commentId}")
    public CommentDto getComment(@PathVariable Long userId,
                                 @PathVariable Long commentId) {
        return privateCommentService.getComment(userId, commentId);
    }

    /**
     * Обновляет существующий комментарий пользователя.
     *
     * @param userId идентификатор пользователя, автора комментария
     * @param commentId идентификатор обновляемого комментария
     * @param newCommentDto DTO с обновленными данными комментария
     * @return обновленный комментарий в формате DTO
     */
    @PatchMapping("{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return privateCommentService.updateComment(userId, commentId, newCommentDto);
    }

    /**
     * Удаляет комментарий пользователя.
     *
     * @param userId идентификатор пользователя, автора комментария
     * @param commentId идентификатор удаляемого комментария
     */
    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        privateCommentService.deleteComment(userId, commentId);
    }

}
