package ru.practicum.ewm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CompilationDto {
    private Long id;

    @NotBlank
    private String title;

    @NotNull
    private Boolean pinned;

    private Set<EventShortDto> events;
}
