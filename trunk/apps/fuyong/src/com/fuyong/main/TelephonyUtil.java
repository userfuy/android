package com.fuyong.main;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-16
 * Time: 下午10:34
 * To change this template use File | Settings | File Templates.
 */
public class TelephonyUtil {
    private static final TelephonyManager tm
            = (TelephonyManager) MyApp.getInstance().getAppContext().getSystemService(Context.TELEPHONY_SERVICE);

    public static String getDeviceId() {
        return tm.getDeviceId();
    }
}
