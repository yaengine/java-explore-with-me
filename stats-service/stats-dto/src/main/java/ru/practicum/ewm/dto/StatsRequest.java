package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class StatsRequest {
    private Set<String> uris;

    private LocalDateTime start;

    private LocalDateTime end;

    private boolean unique;
}
