package com.studyolle.studyolle.modules.account;

import com.studyolle.studyolle.modules.account.query.AccountQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account>, AccountQueryRepository {
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

     Account findByEmail(String email);

    Account  findByNickname(String nickname);
}
