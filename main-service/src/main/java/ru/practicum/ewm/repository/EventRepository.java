package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

    Optional<Event> findByIdAndState(Long eventId, String state);

    boolean existsByCategoryId(Long catId);

    @Query()
    List<Event> findWithFilters(List<Long> users,
                                List<EventState> states,
                                List<Long> categories,
                                LocalDateTime rangeStart,
                                LocalDateTime rangeEnd,
                                Boolean paid,
                                String text,
                                Pageable pageable);
}
