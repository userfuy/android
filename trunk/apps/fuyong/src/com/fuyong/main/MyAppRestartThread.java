package com.fuyong.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-19
 * Time: 下午10:01
 * To change this template use File | Settings | File Templates.
 */
public class MyAppRestartThread extends Thread {
    @Override
    public void run() {
        Context ctx = MyApp.getInstance().getAppContext();
        Intent intent = new Intent(ctx, MainActivity.class);
        PendingIntent restartIntent = PendingIntent.getActivity(ctx, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
