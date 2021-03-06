package com.studyolle.studyolle.event;

import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.domain.Enrollment;
import com.studyolle.studyolle.domain.Event;
import com.studyolle.studyolle.event.validator.EventValidator;
import com.studyolle.studyolle.study.Study;
import com.studyolle.studyolle.study.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final StudyRepository studyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @Transactional
    public Event createEvent(Study study, Event event, Account account) {
        event.setCreatedBy(account);
        event.setStudy(study);
        event.setCreatedDateTime(LocalDateTime.now());
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

    @Transactional
    public void updateEvent(EventForm eventForm, Errors errors, Long id) {
        Event event = eventRepository.findEventEnrollMemberStudyById(id);
        eventValidator.validate(eventForm, errors);
        if(eventForm.getLimitOfEnrollments() < event.getEnrollments().size()){
            errors.rejectValue("limitOfEnrollments", "wrong.input", "최대 등록인원수는 현재 등록한 인원수보다 적을 수 없습니다.");
        }
        if(errors.hasErrors()) return;
        modelMapper.map(eventForm, event);
    }

    @Transactional
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    @Transactional
    public void enroll(Account account, Long id) {
        Event event = eventRepository.findEventEnrollMemberStudyById(id);
        Enrollment enrollment = new Enrollment(account);
        event.addEnrollment(enrollment);
    }

    @Transactional
    public void disEnroll(Account account, Long id) {
        Event event = eventRepository.findEventEnrollMemberStudyById(id);
        Enrollment enrollment = enrollmentRepository.findByAccount(account);
        event.disEnrollment(enrollment);
    }
}
