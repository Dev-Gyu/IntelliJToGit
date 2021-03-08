package com.studyolle.studyolle.modules.event;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.event.notification.EnrollUpdateEvent;
import com.studyolle.studyolle.modules.event.validator.EventValidator;
import com.studyolle.studyolle.modules.study.Study;
import com.studyolle.studyolle.modules.study.StudyRepository;
import com.studyolle.studyolle.modules.study.event.StudyUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;
    private final ApplicationEventPublisher eventPublisher;

    public Event createEvent(Study study, Event event, Account account) {
        event.setCreatedBy(account);
        event.setStudy(study);
        event.setCreatedDateTime(LocalDateTime.now());
        eventPublisher.publishEvent(new StudyUpdateEvent(study, event.getTitle() + "모임이 생성되었습니다."));
        return eventRepository.save(event);
    }

    public List<Event> findNewEvents(String path) {
        List<Event> events = eventRepository.findAllEventEnrollMemberStudyByPath(path);
        List<Event> collect = new ArrayList<>();
        events.forEach(e -> {
            if(LocalDateTime.now().isBefore(e.getEndDateTime()))
                collect.add(e);
        });
        return collect;
    }
    public List<Event> findOldEvents(String path) {
        List<Event> events = eventRepository.findAllEventEnrollMemberStudyByPath(path);
        List<Event> collect = new ArrayList<>();
        events.forEach(e -> {
            if(LocalDateTime.now().isAfter(e.getEndDateTime()))
                collect.add(e);
        });
        return collect;
    }

    public void updateEvent(EventForm eventForm, Errors errors, Long id) {
        Event event = eventRepository.findEventStudyMembersManagersById(id);
        eventValidator.validate(eventForm, errors);
        if(eventForm.getLimitOfEnrollments() < event.getEnrollments().size()){
            errors.rejectValue("limitOfEnrollments", "wrong.input", "최대 등록인원수는 현재 등록한 인원수보다 적을 수 없습니다.");
        }
        if(errors.hasErrors()) return;
        modelMapper.map(eventForm, event);
        Study study = event.getStudy();
        eventPublisher.publishEvent(new StudyUpdateEvent(study,event.getTitle() + "모임이 수정 되었습니다."));
    }

    public void deleteEvent(Event event, Study study) {
        eventRepository.delete(event);
        eventPublisher.publishEvent(new StudyUpdateEvent(study, event.getTitle() + "모임이 취소 되었습니다."));
    }

    public void enroll(Account account, Long id) {
        Event event = eventRepository.findEventEnrollMemberStudyById(id);
        Enrollment enrollment = new Enrollment(account);
        event.addEnrollment(enrollment);
    }

    public void disEnroll(Account account, Long id) {
        Event event = eventRepository.findEventEnrollMemberStudyById(id);
        Enrollment enrollment = enrollmentRepository.findByEventAndAccount(event, account);
        event.disEnrollment(enrollment);
    }

    public void checkin(Event event, Enrollment enrollment) {
        if(!enrollment.isAttended()) {
            enrollment.setAttended(true);
        }
    }
    public void cancelCheckin(Event event, Enrollment enrollment) {
        if(enrollment.isAttended()) {
            enrollment.setAttended(false);
        }
    }

    public void accept(Event event, Enrollment enrollment) {
        if(!enrollment.isAccepted() && event.canAccept(enrollment)){
            enrollment.setAccepted(true);
        }
        eventPublisher.publishEvent(new EnrollUpdateEvent(enrollment, event, "모임 참가 수락되었습니다. 모임일자 확인은 아래 링크를 클릭해주세요."));
    }
    public void reject(Event event ,Enrollment enrollment) {
        if(enrollment.isAccepted() && event.canReject(enrollment)){
            enrollment.setAccepted(false);
        }
        eventPublisher.publishEvent(new EnrollUpdateEvent(enrollment, event, "모임 참가가 거절되었습니다. 아래 링크를 클릭하시면 모임페이지로 이동됩니다."));
    }
}
