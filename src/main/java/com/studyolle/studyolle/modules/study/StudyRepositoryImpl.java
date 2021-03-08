package com.studyolle.studyolle.modules.study;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.QAccount;
import com.studyolle.studyolle.modules.tag.Tag;
import com.studyolle.studyolle.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;

import static com.studyolle.studyolle.modules.account.QAccount.account;
import static com.studyolle.studyolle.modules.study.QStudy.study;
import static com.studyolle.studyolle.modules.tag.QTag.tag;
import static com.studyolle.studyolle.modules.zone.QZone.zone;

public class StudyRepositoryImpl extends QuerydslRepositorySupport implements StudyQueryRepository {
    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    public StudyRepositoryImpl(EntityManager entityManager) {
        super(Study.class);
        this.entityManager = entityManager;
        queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Study> findByKeyword(String keyword, Pageable pageable) {
        JPAQuery<Study> study = queryFactory
                .selectFrom(QStudy.study)
                .distinct()
                .leftJoin(QStudy.study.tags, tag).fetchJoin()
                .leftJoin(QStudy.study.zones, zone).fetchJoin()
                .where(QStudy.study.published.isTrue().and(QStudy.study.title.containsIgnoreCase(keyword))
                        .or(QStudy.study.tags.any().title.containsIgnoreCase(keyword))
                        .or(QStudy.study.zones.any().localNameOfCity.containsIgnoreCase(keyword))
                );
        JPQLQuery<Study> pageableQuery = getQuerydsl().applyPagination(pageable, study);
        QueryResults<Study> studyQueryResults = pageableQuery.fetchResults();
        return new PageImpl<>(studyQueryResults.getResults(), pageable, studyQueryResults.getTotal());
    }

    @Override
    public List<Study> findFirst6StudyByAccount(Account originAccount) {
        return queryFactory
                .selectFrom(study)
                .distinct()
                .join(study.tags, tag).fetchJoin()
                .join(study.zones, zone).fetchJoin()
                .leftJoin(study.members, account).fetchJoin()
                .leftJoin(study.managers, account).fetchJoin()
                .join(account.tags, tag).fetchJoin()
                .join(account.zones, zone).fetchJoin()
                .where(account.email.eq(originAccount.getEmail()))
                .limit(6)
                .offset(0)
                .fetch();

    }
}
