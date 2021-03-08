package com.studyolle.studyolle.modules.account.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.QAccount;
import com.studyolle.studyolle.modules.tag.QTag;
import com.studyolle.studyolle.modules.zone.QZone;

import javax.persistence.EntityManager;
import java.util.List;

import static com.studyolle.studyolle.modules.account.QAccount.account;
import static com.studyolle.studyolle.modules.tag.QTag.tag;
import static com.studyolle.studyolle.modules.zone.QZone.zone;

public class AccountRepositoryImpl implements AccountQueryRepository{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public AccountRepositoryImpl(EntityManager em) {
        this.em = em;
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Account findTagsZonesByEmail(String email) {
        return queryFactory
                .selectFrom(account)
                .join(account.tags, tag).fetchJoin()
                .join(account.zones, zone).fetchJoin()
                .where(account.email.eq(email))
                .fetchOne();
    }
    @Override
    public Account findTagsZonesByNickname(String nickname) {
        return queryFactory
                .selectFrom(account)
                .join(account.tags, tag).fetchJoin()
                .join(account.zones, zone).fetchJoin()
                .where(account.email.eq(nickname))
                .fetchOne();
    }
}
