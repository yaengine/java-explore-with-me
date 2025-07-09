package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.service.AdminCommentService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Контроллер для административных операций с комментариями.
 * Предоставляет API для поиска, просмотра, обновления и удаления комментариев.
 */
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {
    private final AdminCommentService adminCommentService;

    /**
     * Поиск комментариев администратором по фильтрам
     *
     * @param commentText текст для поиска в комментариях
     * @param users список ID пользователей для фильтрации
     * @param events список ID событий для фильтрации
     * @param comments список ID комментариев для фильтрации
     * @param rangeStart начальная дата диапазона для фильтрации
     * @param rangeEnd конечная дата диапазона для фильтрации
     * @param from количество элементов, которые нужно пропустить
     * @param size количество элементов на странице
     * @return список DTO комментариев, удовлетворяющих параметрам поиска
     */
    @GetMapping()
    public List<CommentDto> getComments(
            @RequestParam(required = false) String commentText,
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<Long> comments,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return adminCommentService.getComments(commentText, users, events, comments, rangeStart, rangeEnd, from, size);
    }

    /**
     * Получить все комментарии пользователя
     *
     * @param userId ID пользователя
     * @return список DTO комментариев пользователя
     */
    @GetMapping("/users/{userId}")
    public List<CommentDto> getUserComments(@PathVariable Long userId) {
        return adminCommentService.getUserComment(userId);
    }

    /**
     * Обновляет существующий комментарий.
     *
     * @param commentId ID комментария для обновления
     * @param newCommentDto DTO с новыми данными комментария
     * @return обновленный DTO комментария
     */
    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId,
                                    @RequestBody NewCommentDto newCommentDto) {
        return adminCommentService.updateComment(commentId, newCommentDto);
    }

    /**
     * Удаляет комментарий по его ID.
     *
     * @param commentId ID комментария для удаления
     */
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        adminCommentService.deleteComment(commentId);
    }
}
