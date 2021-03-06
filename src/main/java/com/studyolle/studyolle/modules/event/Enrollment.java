package com.studyolle.studyolle.modules.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.studyolle.studyolle.modules.account.Account;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class Enrollment {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;
    private boolean attended;

    public Enrollment(Account account) {
        this.account = account;
        enrolledAt = LocalDateTime.now();
    }

}
