package edu.eci.dosw.tdd.core.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGeneratorUtil {
    private static final AtomicInteger ID_SEQUENCE = new AtomicInteger(1);

    private IdGeneratorUtil() {
    }

    public static Integer nextId() {
        return ID_SEQUENCE.getAndIncrement();
    }
}
