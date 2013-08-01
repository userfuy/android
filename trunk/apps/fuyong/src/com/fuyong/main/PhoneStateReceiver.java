package com.fuyong.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
//    private static final int EVENT_PHONE_STATE_CHANGED = 1;

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
//    private Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            if (EVENT_PHONE_STATE_CHANGED == msg.what) {
//                notifyObservers();
//            }
//            super.handleMessage(msg);
//        }
//    };

    private PhoneStateReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        MyApp.getInstance().registerReceiver(receiver, intentFilter);
//        try {
//            Class cls = Class.forName("com.android.internal.telephony.CallManager");
//            Method getInstance = cls.getMethod("getInstance");
//            Object callManager = getInstance.invoke(cls);
//            Method registerPhone = cls.getMethod("registerPhone", new Class[]{Phone.class});
//            PhoneFactory.makeDefaultPhone(MyApp.getInstance().getAppContext());
//            Phone phone = PhoneFactory.getDefaultPhone();
//            registerPhone.invoke(callManager, new Object[]{phone});
//            Class[] paraTypes = new Class[]{Handler.class, int.class, Object.class};
//            Method registerForPreciseCallStateChanged = cls.getMethod("registerForPreciseCallStateChanged", paraTypes);
//            registerForPreciseCallStateChanged.invoke(callManager, new Object[]{handler, EVENT_PHONE_STATE_CHANGED, null});
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
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
