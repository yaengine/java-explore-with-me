package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByRequester(Long requesterId);

    boolean existsByRequesterAndEvent(Long requesterId, Long eventId);

    long countByEventAndStatus(Long eventId, RequestStatus status);

    List<ParticipationRequest> findByEvent(Long eventId);

    @Query("SELECT p.event, COUNT(p) FROM ParticipationRequest p " +
            "WHERE p.event IN :eventIds AND p.status = 'CONFIRMED' GROUP BY p.event")
    List<Object[]> countConfirmedRequestsForEvents(@Param("eventIds") List<Long> eventIds);

}
