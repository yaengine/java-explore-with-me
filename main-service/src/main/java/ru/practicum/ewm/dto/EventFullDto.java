package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.enums.EventState;

import java.time.LocalDateTime;

@Data
@SuperBuilder
public class EventFullDto extends EventShortDto {
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @NotBlank
    private String description;

    @NotNull
    private Location location;

    private int participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;

    @NotNull
    private Boolean requestModeration;

    @NotNull
    private EventState state;
}
