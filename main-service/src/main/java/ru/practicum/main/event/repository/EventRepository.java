package ru.practicum.main.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.main.event.Event;
import ru.practicum.main.event.EventState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    Collection<Event> findAllByInitiatorId(Long userId);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    boolean existsByCategoryId(Long categoryId);

    @Query("""
        select e
        from Event e
        where e.state = ru.practicum.main.event.EventState.PUBLISHED
          and (:text is null or lower(e.annotation) like lower(concat('%', :text, '%'))
                or lower(e.description) like lower(concat('%', :text, '%')))
          and (:paid is null or e.paid = :paid)
          and e.eventDate >= :rangeStart
          and (:rangeEnd is null or e.eventDate <= :rangeEnd)
          and (:onlyAvailable = false
                or e.participantLimit = 0
                or (select count(r.id)
                    from Request r
                    where r.event.id = e.id and r.status = ru.practicum.main.request.RequestStatus.CONFIRMED
                ) < e.participantLimit
          )
        """)
    List<Event> findPublicEvents(
            @Param("text") String text,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable
    );

    @Query("""
        select e
        from Event e
        where e.state = ru.practicum.main.event.EventState.PUBLISHED
          and (:text is null or lower(e.annotation) like lower(concat('%', :text, '%'))
                or lower(e.description) like lower(concat('%', :text, '%')))
          and e.category.id in :categories
          and (:paid is null or e.paid = :paid)
          and e.eventDate >= :rangeStart
          and (:rangeEnd is null or e.eventDate <= :rangeEnd)
          and (:onlyAvailable = false
                or e.participantLimit = 0
                or (select count(r.id)
                    from Request r
                    where r.event.id = e.id
                      and r.status = ru.practicum.main.request.RequestStatus.CONFIRMED
                ) < e.participantLimit
          )
        """)
    List<Event> findPublicEventsByCategories(
            @Param("text") String text,
            @Param("categories") Set<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("onlyAvailable") boolean onlyAvailable
    );

    @Query("""
        select e
        from Event e
        where (:filterUsers = false or e.initiator.id in :users)
          and (:filterStates = false or e.state in :states)
          and (:filterCategories = false or e.category.id in :categories)
          and (:rangeStart is null or e.eventDate >= :rangeStart)
          and (:rangeEnd is null or e.eventDate <= :rangeEnd)
        """)
    List<Event> findAdminEvents(
            @Param("filterUsers") boolean filterUsers,
            @Param("users") Set<Long> users,
            @Param("filterStates") boolean filterStates,
            @Param("states") Set<EventState> states,
            @Param("filterCategories") boolean filterCategories,
            @Param("categories") Set<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd
    );

}
