package com.studyolle.studyolle.modules.event;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.event.query.EnrollmentQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, EnrollmentQueryRepository {
    Enrollment findByAccount(Account account);

    Enrollment findByEventAndAccount(Event event, Account account);

    @Query("select en from Enrollment en join fetch en.account where en.id = :id")
    Enrollment findEnrollAccountById(@Param("id") Long id);

}
