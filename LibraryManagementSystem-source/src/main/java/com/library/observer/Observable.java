package com.library.observer;

public interface Observable {
    void addObserver(LibraryObserver observer);
    void removeObserver(LibraryObserver observer);
    void notifyObservers(String eventType, String message, String targetId);
}
