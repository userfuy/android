package com.fuyong.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Observable;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-20
 * Time: 下午2:14
 * To change this template use File | Settings | File Templates.
 */
public class PhoneStateReceiver extends Observable {
    private static PhoneStateReceiver instance;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {

            } else {
                setChanged();
                notifyObservers(new Integer(TelephonyUtil.getTelephonyManager().getCallState()));
            }
        }
    };

    private PhoneStateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        MyApp.getInstance().registerReceiver(receiver, intentFilter);
    }

    synchronized public static PhoneStateReceiver getInstance() {
        if (null == instance) {
            instance = new PhoneStateReceiver();
        }
        return instance;
    }

    synchronized public void release() {
        MyApp.getInstance().unregisterReceiver(receiver);
        instance = null;
    }
}
