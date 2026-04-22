package com.library.util;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger userCounter = new AtomicInteger(1000);
    private static final AtomicInteger bookCounter = new AtomicInteger(1000);

    public static String newUserId()    { return "USR-" + userCounter.getAndIncrement(); }
    public static String newBookId()    { return "BK-"  + bookCounter.getAndIncrement(); }
    public static String newUUID(String prefix) {
        return prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
