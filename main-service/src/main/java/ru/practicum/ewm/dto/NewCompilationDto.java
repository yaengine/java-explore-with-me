package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class NewCompilationDto {
    @NotBlank
    private String title;

    private Boolean pinned = false;

    private Set<Long> events;
}
