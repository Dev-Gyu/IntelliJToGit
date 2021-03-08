package com.studyolle.studyolle.modules.study;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyRepository extends JpaRepository<Study, Long>, StudyQueryRepository{
    boolean existsByPath(String path);

    @EntityGraph(value = "Study.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Study findByPath(String path);

    @Query("select s from Study s left join fetch s.tags t left join fetch s.managers m where s.path = :path")
    Study findTagsManagerByPath(@Param("path") String path);

    @Query("select s from Study s left join fetch s.zones z left join fetch s.managers m where s.path = :path")
    Study findZonesManagersByPath(@Param("path") String path);

    @Query("select s from Study s join fetch s.managers m where s.path = :path")
    Study findStudyManagerByPath(@Param("path") String path);

    Study findOnlyStudyByPath(String path);

    @Query("select s from Study s left join fetch s.members m where s.path = :path")
    Study findStudyMemberByPath(@Param("path") String path);

    @Query("select distinct s from Study s left join fetch s.tags left join fetch s.zones where s.id = :id")
    Study findStudyWithTagsAndZonesById(@Param("id") Long id);

    @Query("select distinct s from Study s left join fetch s.members mb join fetch s.managers ms where s.id = :id")
    Study findStudyManagersMembersById(@Param("id") Long id);
}
