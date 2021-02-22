package com.jump.standard.commons.snowflake;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class SnowflakeGenerator {
    private static final long EPOCH = 1514736000000L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_MASK = 4095L;
    private static final long WORKER_ID_LEFT_SHIFT_BITS = 12L;
    private static final long TIMESTAMP_LEFT_SHIFT_BITS = 22L;
    private static final long BACKUP_COUNT = 2L;
    private static final long WORKER_ID_MAX_VALUE = 341L;
    private static long workerId = 0L;
    private long sequence;
    private long lastTime;
    private static Map<Long, Long> workerIdLastTimeMap = new ConcurrentHashMap();
    private static final long MAX_BACKWARD_MS = 4L;

    public SnowflakeGenerator() {
    }

    public static void setWorkerId(long workerId) {
        if (workerId >= SnowflakeGenerator.workerId && workerId <= WORKER_ID_MAX_VALUE) {
            SnowflakeGenerator.workerId = workerId;
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", WORKER_ID_MAX_VALUE));
        }
    }

    public synchronized long nextId() {
        long currentMillis = System.currentTimeMillis();
        if (this.lastTime > currentMillis) {
            if (this.lastTime - currentMillis < MAX_BACKWARD_MS) {
                try {
                    TimeUnit.MILLISECONDS.sleep(this.lastTime - currentMillis);
                } catch (InterruptedException e) {
                }
            } else {
                this.tryGenerateKeyOnBackup(currentMillis);
                System.err.println("触发了时钟回拨机制");
            }
        }

        if (this.lastTime == currentMillis) {
            if (0L == (this.sequence = ++this.sequence & SEQUENCE_MASK)) {
                currentMillis = this.waitUntilNextTime(currentMillis);
            }
        } else {
            this.sequence = 0L;
        }

        this.lastTime = currentMillis;
        workerIdLastTimeMap.put(workerId, this.lastTime);
        return currentMillis - EPOCH << TIMESTAMP_LEFT_SHIFT_BITS | workerId << WORKER_ID_LEFT_SHIFT_BITS | this.sequence;
    }

    private long tryGenerateKeyOnBackup(long currentMillis) {
        Iterator iterator = workerIdLastTimeMap.entrySet().iterator();

        do {
            if (!iterator.hasNext()) {
                throw new IllegalStateException("Clock is moving backwards, current time is " + currentMillis + " milliseconds, workerId map = " + workerIdLastTimeMap);
            }

            Entry<Long, Long> entry = (Entry)iterator.next();
            workerId = (Long)entry.getKey();
            Long tempLastTime = (Long)entry.getValue();
            this.lastTime = tempLastTime == null ? 0L : tempLastTime;
        } while(this.lastTime > currentMillis);

        return this.lastTime;
    }

    private long waitUntilNextTime(long lastTime) {
        long time;
        for(time = System.currentTimeMillis(); time <= lastTime; time = System.currentTimeMillis()) {
        }

        return time;
    }

    static {
        for(int i = 0; (long)i <= BACKUP_COUNT; ++i) {
            workerIdLastTimeMap.put(workerId + (long)i * WORKER_ID_MAX_VALUE, 0L);
        }

    }
}
