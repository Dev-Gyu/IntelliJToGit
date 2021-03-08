package com.studyolle.studyolle.modules.event.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.QAccount;
import com.studyolle.studyolle.modules.event.Enrollment;
import com.studyolle.studyolle.modules.event.QEnrollment;
import com.studyolle.studyolle.modules.event.QEvent;
import com.studyolle.studyolle.modules.study.QStudy;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.studyolle.studyolle.modules.account.QAccount.account;
import static com.studyolle.studyolle.modules.event.QEnrollment.enrollment;
import static com.studyolle.studyolle.modules.event.QEvent.event;
import static com.studyolle.studyolle.modules.study.QStudy.study;

@Transactional(readOnly = true)
public class EnrollmentRepositoryImpl implements EnrollmentQueryRepository{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public EnrollmentRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Enrollment> findEnrollStudyByAccountAndAccepted(Account originAccount, boolean accepted) {
        return queryFactory
                .select(enrollment)
                .from(enrollment)
                .join(enrollment.account, account).fetchJoin()
                .join(enrollment.event, event).fetchJoin()
                .join(event.study, study).fetchJoin()
                .where(account.email.eq(originAccount.getEmail()).and(enrollment.accepted.isTrue()))
                .limit(4)
                .offset(0)
                .fetch();
    }
}
