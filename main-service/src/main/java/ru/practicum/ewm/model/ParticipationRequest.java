package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.Builder;
import ru.practicum.ewm.enums.RequestStatus;

import java.time.LocalDateTime;

@Builder
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long event;

    @Column(name = "requester_id", nullable = false)
    private Long requester;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(nullable = false)
    private LocalDateTime created;
}
