package com.studyolle.studyolle.event;

import com.studyolle.studyolle.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("select e from Event e join fetch e.study s left join fetch e.enrollments where s.path = :path")
    List<Event> findAllEventEnrollMemberStudyByPath(@Param("path") String path);

    @Query("select e from Event e join fetch e.createdBy a join fetch e.study s left join fetch e.enrollments er where e.id = :id")
    Optional<Event> findAllEventEnrollMemberStudyById(@Param("id") Long id);

    @Query("select e from Event e left join fetch e.enrollments er left join fetch er.account where e.id = :id")
    Event findEventEnrollMemberStudyById(Long id);
}
