package com.studyolle.studyolle.modules.notification;

import com.studyolle.studyolle.modules.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByAccountAndChecked(Account account, boolean checked);

    List<Notification> findNotificationsByAccountAndChecked(Account account, boolean checked);

    void deleteCheckedByAccountAndChecked(Account account, boolean checked);
}
