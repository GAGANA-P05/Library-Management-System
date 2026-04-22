package com.library.observer;

public interface LibraryObserver {
    void update(String eventType, String message, String targetId);
}
