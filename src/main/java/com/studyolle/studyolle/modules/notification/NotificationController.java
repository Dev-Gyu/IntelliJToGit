package com.studyolle.studyolle.modules.notification;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String notifications(@CurrentUser Account account, Model model){
        List<Notification> newNotifications = notificationRepository.findNotificationsByAccountAndChecked(account, false);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);
        newNotifications.forEach(notification -> notification.setChecked(true));

        List<Notification> studyUpdates = notificationService.sliceStudyUpdate(newNotifications);
        List<Notification> newStudies = notificationService.sliceNewStudy(newNotifications);
        List<Notification> eventInfos = notificationService.sliceEventInfo(newNotifications);

        model.addAttribute("isNew", true);
        model.addAttribute("studyUpdates", studyUpdates);
        model.addAttribute("newStudies", newStudies);
        model.addAttribute("eventInfos", eventInfos);
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        return "notification/newNotification";
    }

    @GetMapping("/notifications/old")
    public String notifications_old(@CurrentUser Account account, Model model){
        List<Notification> newNotifications = notificationRepository.findNotificationsByAccountAndChecked(account, true);
        long numberOfNotChecked = notificationRepository.countByAccountAndChecked(account, false);
        long numberOfChecked = notificationRepository.countByAccountAndChecked(account, true);

        List<Notification> studyUpdates = notificationService.sliceStudyUpdate(newNotifications);
        List<Notification> newStudies = notificationService.sliceNewStudy(newNotifications);
        List<Notification> eventInfos = notificationService.sliceEventInfo(newNotifications);

        model.addAttribute("isNew", false);
        model.addAttribute("studyUpdates", studyUpdates);
        model.addAttribute("newStudies", newStudies);
        model.addAttribute("eventInfos", eventInfos);
        model.addAttribute("numberOfNotChecked", numberOfNotChecked);
        model.addAttribute("numberOfChecked", numberOfChecked);
        return "notification/newNotification";
    }
    @DeleteMapping("/notifications")
    public String notifications_delete(@CurrentUser Account account, Model model){
        notificationRepository.deleteCheckedByAccountAndChecked(account, true);

        return "redirect:/notifications";
    }
}
