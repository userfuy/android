package com.fuyong.main;

import android.net.TrafficStats;
import android.os.Process;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-8-5
 * Time: 下午7:29
 * To change this template use File | Settings | File Templates.
 */
public class MyTrafficStatsTask extends Observable implements Runnable {
    static private MyTrafficStatsTask instance;

    private long mobileRxBytes = 0;
    private long mobileTxBytes = 0;
    private long totalRxBytes = 0;
    private long totalTxBytes = 0;
    private long uidRxBytes = 0;
    private long uidTxBytes = 0;
    private float mobileRxkps = 0;
    private float mobileTxkps = 0;
    private float totalRxkps = 0;
    private float totalTxkps = 0;
    private float uidRxkps = 0;
    private float uidTxkps = 0;
    private ScheduledFuture<?> futureTask;

    private MyTrafficStatsTask() {
    }

    synchronized public static MyTrafficStatsTask getInstance() {
        if (null == instance) {
            instance = new MyTrafficStatsTask();
        }
        return instance;
    }

    public void registerObserver(Observer observer) {
        if (countObservers() == 0) {
            init();
        }
        addObserver(observer);
    }

    public void unRegisterObserver(Observer observer) {
        deleteObserver(observer);
        if (countObservers() == 0) {
            futureTask.cancel(true);
        }
    }

    private void init() {
        futureTask = MyScheduledThreadPool.getExecutor().scheduleAtFixedRate(this, 1, 1, TimeUnit.SECONDS);
        mobileRxBytes = TrafficStats.getMobileRxBytes();
        mobileTxBytes = TrafficStats.getMobileTxBytes();
        totalRxBytes = TrafficStats.getTotalRxBytes();
        totalTxBytes = TrafficStats.getTotalTxBytes();
        uidRxBytes = TrafficStats.getUidRxBytes(Process.myUid());
        uidTxBytes = TrafficStats.getUidTxBytes(Process.myUid());
    }

    @Override
    public void run() {
        long bytes;
        bytes = TrafficStats.getMobileRxBytes();
        mobileRxkps = 8f * (bytes - mobileRxBytes) / 1000;
        mobileRxBytes = bytes;

        bytes = TrafficStats.getMobileTxBytes();
        mobileTxkps = 8f * (bytes - mobileTxBytes) / 1000;
        mobileTxBytes = bytes;

        bytes = TrafficStats.getTotalRxBytes();
        totalRxkps = 8f * (bytes - totalRxBytes) / 1000;
        totalRxBytes = bytes;

        bytes = TrafficStats.getTotalTxBytes();
        totalTxkps = 8f * (bytes - totalTxBytes) / 1000;
        totalTxBytes = bytes;

        bytes = TrafficStats.getUidRxBytes(Process.myUid());
        uidRxkps = 8f * (bytes - uidRxBytes) / 1000;
        uidRxBytes = bytes;

        bytes = TrafficStats.getUidTxBytes(Process.myUid());
        uidTxkps = 8f * (bytes - uidTxBytes) / 1000;
        uidTxBytes = bytes;

        setChanged();
        notifyObservers();
    }

    public float getUidRxkps() {
        return uidRxkps;
    }
}
