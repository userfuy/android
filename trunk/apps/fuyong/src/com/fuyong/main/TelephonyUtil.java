package com.fuyong.main;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-16
 * Time: 下午10:34
 * To change this template use File | Settings | File Templates.
 */
public class TelephonyUtil {
    private static Logger log = Log.getLogger(Log.MY_APP);
    private static final TelephonyManager tm
            = (TelephonyManager) MyApp.getInstance().getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
    private static ITelephony telephony = ITelephony.Stub.asInterface(ServiceManager.checkService("phone"));

//    static {
//        Class<TelephonyManager> c = TelephonyManager.class;
//        try {
//            Method method = c.getDeclaredMethod("getITelephony", (Class[]) null);
//            method.setAccessible(true);
//            telephony = (ITelephony) method.invoke(tm, (Object[]) null);
//        } catch (InvocationTargetException e) {
//            log.error(e);
//        } catch (NoSuchMethodException e) {
//            log.error(e);
//        } catch (IllegalAccessException e) {
//            log.error(e);
//        }
//    }

    public static TelephonyManager getTelephonyManager() {
        return tm;
    }

    public static String getDeviceId() {
        return tm.getDeviceId();
    }

    public static boolean isAirModeOn() {
        return (Settings.System.getInt(MyApp.getInstance().getAppContext().getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) == 1 ? true : false);
    }

    public static void setAirplaneMode(boolean enable) {
        Settings.System.putInt(MyApp.getInstance().getAppContext().getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, enable ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", enable);
        MyApp.getInstance().getAppContext().sendBroadcast(intent);
    }

    public static boolean call(String s) {
        log.info("call " + s);

        try {
            if (telephony.isRadioOn()) {
                telephony.call(s);
                return true;
            }
            return false;
        } catch (RemoteException e) {
            log.error(e);
            return false;
        }
    }

    public static boolean endCall() {
        log.info("end call");
        try {
            telephony.endCall();
        } catch (RemoteException e) {
            log.error(e);
            return false;
        }
        return true;
    }

    public static void answerRingingCall() {
        log.info("answer incoming call");
        try {
            telephony.answerRingingCall();
        } catch (RemoteException e) {
            log.error(e);
        }
    }
}
