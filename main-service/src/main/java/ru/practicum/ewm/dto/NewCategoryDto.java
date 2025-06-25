package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    @NotBlank(message = "Название категории не должно быть пустым")
    private String name;
}
