package com.studyolle.studyolle.modules.event.notification;

import com.studyolle.studyolle.infra.config.AppProperties;
import com.studyolle.studyolle.infra.mail.EmailMessage;
import com.studyolle.studyolle.infra.mail.EmailService;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.event.Enrollment;
import com.studyolle.studyolle.modules.event.EnrollmentRepository;
import com.studyolle.studyolle.modules.event.Event;
import com.studyolle.studyolle.modules.event.EventRepository;
import com.studyolle.studyolle.modules.notification.Notification;
import com.studyolle.studyolle.modules.notification.NotificationRepository;
import com.studyolle.studyolle.modules.notification.NotificationType;
import com.studyolle.studyolle.modules.study.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class EnrollEventListener {
    private final EventRepository eventRepository;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @EventListener
    public void handleEnrollUpdateListener(EnrollUpdateEvent enrollUpdateEvent) {

        Event event = eventRepository.findEventEnrollMemberStudyById(enrollUpdateEvent.getEvent().getId());
        String link = "/study/" + URLEncoder.encode(event.getStudy().getPath(), StandardCharsets.UTF_8) + "/events/" + event.getId();
        Enrollment enrollment = enrollUpdateEvent.getEnrollment();

        List<Enrollment> enrollments = event.getEnrollments();
        if (enrollments.contains(enrollment)) {
            if (enrollment.getAccount().isStudyEnrollmentResultByEmail()) {
                sendEmail(enrollUpdateEvent, event, link, enrollment);

            } else if (enrollment.getAccount().isStudyEnrollmentResultByWeb()) {
                saveNotification(event, link, enrollment, enrollUpdateEvent.getMessage());
            }
        }
    }

    private void sendEmail(EnrollUpdateEvent enrollUpdateEvent, Event event, String link, Enrollment enrollment) {
        Context context = new Context();
        context.setVariable("nickname", enrollment.getAccount().getNickname());
        context.setVariable("message", enrollUpdateEvent.getMessage());
        context.setVariable("host", appProperties.getHost());
        context.setVariable("link", link);
        context.setVariable("linkName", "클릭하시면 모임으로 이동합니다.");
        String result = templateEngine.process("email/simple-link", context);

        EmailMessage emailMessage = EmailMessage.builder()
                .to(enrollment.getAccount().getEmail())
                .subject("스터디올레, '" + event.getTitle() + "' 모임 참가 신청 결과가 변동되었습니다.")
                .message(result)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void saveNotification(Event event, String link, Enrollment enrollment, String message) {
        Notification notification = new Notification(event.getTitle(), link, message
                , enrollment.getAccount(), LocalDateTime.now(), NotificationType.EVENT_ENROLLMENT);
        notificationRepository.save(notification);
    }
}
