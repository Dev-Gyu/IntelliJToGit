package com.studyolle.studyolle.modules.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;


    public List<Notification> sliceNewStudy(List<Notification> newNotifications) {
        List<Notification> temp = new ArrayList<>();
        for (Notification newNotification : newNotifications) {
            if(newNotification.getNotificationType() == NotificationType.STUDY_CREATED)
                temp.add(newNotification);
        }
        if(temp.size() == 0) {
            return Collections.emptyList();
        }
        return temp;
    }

    public List<Notification> sliceStudyUpdate(List<Notification> newNotifications) {
        List<Notification> temp = new ArrayList<>();
        for (Notification newNotification : newNotifications) {
            if(newNotification.getNotificationType() == NotificationType.STUDY_UPDATE)
                temp.add(newNotification);
        }
        if(temp.size() == 0) {
            return Collections.emptyList();
        }
        return temp;
    }

    public List<Notification> sliceEventInfo(List<Notification> newNotifications) {
        List<Notification> temp = new ArrayList<>();
        for (Notification newNotification : newNotifications) {
            if(newNotification.getNotificationType() == NotificationType.EVENT_ENROLLMENT)
                temp.add(newNotification);
        }
        if(temp.size() == 0) {
            return Collections.emptyList();
        }
        return temp;
    }
}
