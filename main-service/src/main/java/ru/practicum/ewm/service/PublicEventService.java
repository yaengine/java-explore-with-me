package ru.practicum.ewm.service;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.enums.EventSort;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicEventService {
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    public List<EventShortDto> getPublishedEvents(String text, List<Long> categories, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean isAvailable, EventSort eventSort, int from, int size,
                                                  HttpServletRequest request) {

        statsClient.sendHit(new EndpointHitDto("ExploreWithMe", request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now()));

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        Pageable pageable = PageRequest.of(from / size, size, getSort(eventSort));

        List<Event> events = eventRepository.findWithFilters(null, List.of(EventState.PUBLISHED),
                categories, rangeStart, rangeEnd, paid,
                StringUtils.isNotBlank(text) ? text : null,
                pageable);
        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();

        Map<Long, Long> confirmedRequests = participationRequestRepository.countConfirmedRequestsForEvents(eventIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        Map<String, Event> uriToEventMap = events.stream()
                .collect(Collectors.toMap(e -> "/events/" + e.getId(), e -> e));

        StatsRequest statsRequest = StatsRequest.builder()
                .uris(uriToEventMap.keySet())
                .unique(true)
                .build();

        List<ViewStats> stats = statsClient.getStats(List.of(statsRequest));

        Map<String, Long> viewsMap = stats.stream()
                .collect(Collectors.toMap(ViewStats::getUri, ViewStats::getHits));

        return events.stream()
                .filter(event -> !isAvailable ||
                        confirmedRequests.getOrDefault(event.getId(), 0L) < event.getParticipantLimit())
                .map(event -> {
                    EventShortDto dto = EventMapper.toEventShortDto(event);
                    dto.setConfirmedRequests(confirmedRequests.getOrDefault(event.getId(), 0L));
                    dto.setViews(viewsMap.getOrDefault("/events/" + event.getId(), 0L));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public EventFullDto getPublishedEventById(Long eventId, HttpServletRequest request) {
        statsClient.sendHit(new EndpointHitDto("ExploreWithMe",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));

        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED.name())
                .orElseThrow(() -> new ValidationException("Событие с id=" + eventId + " не найдено.",
                        HttpStatus.NOT_FOUND));

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        eventFullDto.setConfirmedRequests(participationRequestRepository.countByEventAndStatus(eventId,
                RequestStatus.CONFIRMED));
        StatsRequest statsRequest = StatsRequest.builder()
                .uris(Set.of("/events/" + eventId))
                .unique(true)
                .build();
        List<ViewStats> stats = statsClient.getStats(List.of(statsRequest));
        eventFullDto.setViews(stats.isEmpty() ? 0L : stats.getFirst().getHits());
        return eventFullDto;
    }

        private Sort getSort(EventSort sort) {
            return sort == EventSort.VIEWS ? Sort.by(Sort.Direction.DESC, EventSort.VIEWS.name()) :
                    Sort.by(Sort.Direction.ASC, EventSort.EVENT_DATE.name());
        }

}
