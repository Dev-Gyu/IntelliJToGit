package com.studyolle.studyolle.event;

import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.domain.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Enrollment findByAccount(Account account);
}
