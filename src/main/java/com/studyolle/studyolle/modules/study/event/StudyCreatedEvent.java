package com.studyolle.studyolle.modules.study.event;

import com.studyolle.studyolle.modules.study.Study;
import lombok.Data;

@Data
public class StudyCreatedEvent {
    private Study study;

    public StudyCreatedEvent(Study study){
        this.study = study;
    }
}
