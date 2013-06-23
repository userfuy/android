package com.fuyong.main;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.fuyong.main.test.TestManager;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-10
 * Time: 下午7:48
 * To change this template use File | Settings | File Templates.
 */
public class MainService extends Service {
    private final IBinder binder = new MyBinder();

    public class MyBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);    //To change body of overridden methods use File | Settings | File Templates.
    }


    public TestManager getTestManager() {
        return TestManager.getInstance();
    }
}
