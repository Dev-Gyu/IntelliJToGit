package com.studyolle.studyolle.modules.event.notification;

import com.studyolle.studyolle.modules.event.Enrollment;
import com.studyolle.studyolle.modules.event.Event;
import com.studyolle.studyolle.modules.study.Study;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EnrollUpdateEvent {
    private Enrollment enrollment;
    private String message;
    private Event event;

    public EnrollUpdateEvent(Enrollment enrollment, Event event, String message) {
        this.enrollment = enrollment;
        this.message = message;
        this.event = event;
    }
}
