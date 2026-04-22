package com.library.observer;

public class MaintenanceStaffObserver implements LibraryObserver {

    @Override
    public void update(String eventType, String message, String targetId) {
        if ("BOOK_DAMAGED".equals(eventType)) {
            System.out.println("[MAINTENANCE ALERT] Staff notified: " + message);
        } else if ("SECURITY_ALERT".equals(eventType)) {
            System.out.println("[SECURITY ALERT] Security notified: " + message);
        }
    }
}
