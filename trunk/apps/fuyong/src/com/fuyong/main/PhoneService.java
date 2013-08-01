package com.fuyong.main;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-21
 * Time: 上午1:09
 * To change this template use File | Settings | File Templates.
 */
public class PhoneService extends Service {
    private final IBinder binder = new MyBinder();

    public PhoneService() {
    }


    public class MyBinder extends Binder {
        PhoneService getService() {
            return PhoneService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
