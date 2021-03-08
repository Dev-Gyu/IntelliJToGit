package com.studyolle.studyolle.modules.account.query;

import com.studyolle.studyolle.modules.account.Account;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AccountQueryRepository {
    Account findTagsZonesByEmail(String Email);
    Account findTagsZonesByNickname(String nickname);
}
