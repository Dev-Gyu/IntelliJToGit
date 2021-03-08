package com.studyolle.studyolle.modules.event.query;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.event.Enrollment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EnrollmentQueryRepository {
    List<Enrollment> findEnrollStudyByAccountAndAccepted(Account account, boolean accepted);
}
