package ru.practicum.ewm.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.UserService;

import java.util.List;

/**
 * Контроллер для административного управления пользователями.
 * Предоставляет API для регистрации, просмотра и удаления пользователей.
 */
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    /**
     * Получение информации о пользователях администратором
     *
     * @param ids  список идентификаторов пользователей для фильтрации (опционально)
     * @param from количество элементов, которые нужно пропустить (по умолчанию 0)
     * @param size количество элементов в ответе (по умолчанию 10)
     * @return список DTO пользователей, удовлетворяющих условиям выборки
     */
    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return userService.getUsers(ids, from, size);
    }

    /**
     * Добавление нового пользователя администратором
     *
     * @param newUserRequest DTO с данными нового пользователя
     * @return DTO зарегистрированного пользователя
     * @responseStatus 201 CREATED
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto registerUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userService.registerUser(newUserRequest);
    }

    /**
     * Удаление пользователя администратором
     *
     * @param userId идентификатор пользователя для удаления
     * @responseStatus 204 NO_CONTENT
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
