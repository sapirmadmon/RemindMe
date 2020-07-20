package com.example.remindme;

import java.util.concurrent.atomic.AtomicInteger;

public class Sequence {
    private static final AtomicInteger counter = new AtomicInteger();

    public static int nextValue() {
        return counter.getAndIncrement();
    }
}
