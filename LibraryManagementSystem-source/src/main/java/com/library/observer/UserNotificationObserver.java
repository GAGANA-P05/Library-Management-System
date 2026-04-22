package com.library.observer;

import com.library.model.Notification;
import com.library.repository.NotificationRepository;
import java.util.UUID;

public class UserNotificationObserver implements LibraryObserver {
    private final NotificationRepository notificationRepository;

    public UserNotificationObserver(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void update(String eventType, String message, String targetId) {
        String notifId = "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Notification notification = new Notification(notifId, targetId, message);
        notificationRepository.save(notification);
        System.out.println("[NOTIFICATION] To User " + targetId + ": " + message);
    }
}
