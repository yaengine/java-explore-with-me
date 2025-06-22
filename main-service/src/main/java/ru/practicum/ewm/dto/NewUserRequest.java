package ru.practicum.ewm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
    @Email(message = "Email должен иметь формат адреса электронной почты")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;
}
