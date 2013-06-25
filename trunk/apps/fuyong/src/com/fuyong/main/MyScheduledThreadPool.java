package com.fuyong.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-25
 * Time: 下午7:30
 * To change this template use File | Settings | File Templates.
 */
public class MyScheduledThreadPool {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }

    public static void shutdown() {
        executor.shutdown();
    }

    public static void shutdownNow() {
        executor.shutdownNow();
    }
}
