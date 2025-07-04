package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.enums.UserStateAction;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

    private static final Map<UserStateAction, EventState> statusMap = Map.of(
            UserStateAction.CANCEL_REVIEW, EventState.CANCELED,
            UserStateAction.SEND_TO_REVIEW, EventState.PENDING
    );

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователь с id " + userId + " не найден",
                        HttpStatus.NOT_FOUND));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new ValidationException("Категория с id " + newEventDto.getCategory() +
                        " не найдена", HttpStatus.NOT_FOUND));

        Event event = eventMapper.toEvent(newEventDto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());

        event = eventRepository.save(event);
        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ValidationException("Событие с id " + eventId + " не найдено",
                        HttpStatus.NOT_FOUND));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Изменять событие может только инициатор", HttpStatus.FORBIDDEN);
        }

        if (event.getState() == EventState.PUBLISHED) {
            throw new ValidationException("Опубликованное событие нельзя редактировать", HttpStatus.CONFLICT);
        }

        if (event.getState() != EventState.PENDING && event.getState() != EventState.CANCELED) {
            throw new ValidationException("Изменять можно только отменённые события или события в ожидании публикации",
                    HttpStatus.CONFLICT);
        }

        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время события не могут быть раньше, чем через 2 часа " +
                    "от текущего момента",
                    HttpStatus.BAD_REQUEST);
        }

        updateField(updateRequest.getTitle(), event::setTitle);
        updateField(updateRequest.getAnnotation(), event::setAnnotation);
        updateField(updateRequest.getDescription(), event::setDescription);

        if (updateRequest.getCategory() != null) {
            var category = categoryRepository.getCategoryById(updateRequest.getCategory());
            if (category == null) {
                throw new ValidationException("Категории " + updateRequest.getCategory() + "не существвует",
                        HttpStatus.NOT_FOUND);
            }
            event.setCategory(category);
        }

        updateField(updateRequest.getEventDate(), event::setEventDate);
        updateField(updateRequest.getLocation(), event::setLocation);
        updateField(updateRequest.getPaid(), event::setPaid);
        updateField(updateRequest.getParticipantLimit(), event::setParticipantLimit);

        event.setState(Optional.ofNullable(updateRequest.getStateAction())
                .map(statusMap::get)
                .orElse(EventState.PENDING));

        Event updated = eventRepository.save(event);
        EventFullDto dto = eventMapper.toEventFullDto(updated);
        dto.setConfirmedRequests(participationRequestRepository.countByEventAndStatus(eventId, RequestStatus.CONFIRMED));
        return dto;
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "createdOn"));

        if (!userService.userExists(userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не найден", HttpStatus.NOT_FOUND);
        }

        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        if (events.isEmpty()) return List.of();

        // Считаем сколько заявок подтверждено
        Map<Long, Long> confirmedRequestsMap = getconfirmedRequestsMap(events);

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
                .map(event -> {
                    EventShortDto dto = eventMapper.toEventShortDto(event);
                    dto.setConfirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0L));
                    dto.setViews(viewsMap.getOrDefault("/events/" + event.getId(), 0L));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventFullDto getUserEventById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new ValidationException("Событие с id=" + eventId + " не найдено или не " +
                        "принадлежит пользователю id " + userId, HttpStatus.NOT_FOUND));

        EventFullDto eventFullDto = eventMapper.toEventFullDto(event);
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

    @Transactional(readOnly = true)
    private Map<Long, Long> getconfirmedRequestsMap(List<Event> events) {
        List<Long> eventIds = events.stream().map(Event::getId).toList();

        return participationRequestRepository
                .countConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    private <T> void updateField(T value, Consumer<T> setter) {
        if (value != null) setter.accept(value);
    }
}
