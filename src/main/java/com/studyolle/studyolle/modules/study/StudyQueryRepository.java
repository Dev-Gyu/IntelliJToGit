package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StudyQueryRepository {
    Page<Study> findByKeyword(String keyword, Pageable pageable);
    List<Study> findFirst6StudyByAccount(Account account);
}
