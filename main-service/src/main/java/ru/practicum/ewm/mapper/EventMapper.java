package ru.practicum.ewm.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.model.Event;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {
    static CategoryMapper categoryMapper;
    static UserMapper userMapper;

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit() != null ?
                        event.getParticipantLimit() : 0)
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration() != null ?
                        event.getRequestModeration() : true)
                .state(event.getState())
                .build();
    }

   public static List<EventFullDto> toEventFullDtoList(List<Event> events) {
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryMapper.toCategoryDto(event.getCategory()))
                .eventDate(event.getEventDate())
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .paid(event.isPaid())
                .title(event.getTitle())
                .build();
    }

    public static Set<EventShortDto> toEventShortDtoSet(Set<Event> events) {
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
