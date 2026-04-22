package com.library.model;

import java.time.LocalDateTime;

public class Notification {
    private String notificationId;
    private String userId;
    private String message;
    private LocalDateTime dateSent;

    public Notification(String notificationId, String userId, String message) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.dateSent = LocalDateTime.now();
    }

    public String getNotificationId() { return notificationId; }
    public String getUserId() { return userId; }
    public String getMessage() { return message; }
    public LocalDateTime getDateSent() { return dateSent; }

    @Override
    public String toString() {
        return "Notification{id='" + notificationId + "', userId='" + userId +
               "', message='" + message + "', sent=" + dateSent + "}";
    }
}
