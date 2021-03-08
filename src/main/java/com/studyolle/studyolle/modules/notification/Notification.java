package com.studyolle.studyolle.modules.notification;

import com.studyolle.studyolle.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private Account account;

    private LocalDateTime createLocalDateTime;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    public Notification(String title, String link, String message, Account account, LocalDateTime createLocalDateTime, NotificationType notificationType) {
        this.title = title;
        this.link = link;
        this.message = message;
        this.account = account;
        this.createLocalDateTime = createLocalDateTime;
        this.notificationType = notificationType;
    }
}
