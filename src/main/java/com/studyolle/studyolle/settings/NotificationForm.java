package com.studyolle.studyolle.settings;

import com.studyolle.studyolle.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class NotificationForm {
    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

}
