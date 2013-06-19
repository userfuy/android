package com.fuyong.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-16
 * Time: 下午9:12
 * To change this template use File | Settings | File Templates.
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static UncaughtExceptionHandler instance;
    private Logger log = Log.getLogger(Log.CRASH);
    private final Context myContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infoMap = new HashMap<String, String>();

    private UncaughtExceptionHandler() {
        myContext = null;
    }

    private UncaughtExceptionHandler(Context context) {
        myContext = context;
    }

    synchronized public static UncaughtExceptionHandler getInstance() {
        if (null == instance) {
            instance = new UncaughtExceptionHandler(MyApp.getInstance().getAppContext());
        }
        return instance;
    }

    public void uncaughtException(Thread thread, Throwable exception) {
        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);
        log.error(stackTrace.toString());

        collectDeviceInfo();
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infoMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        log.error(sb);

        new MyToastThread(myContext.getString(R.string.crash_msg)).start();
        // 重启应用
        new MyAppRestartThread().start();
    }

    private void collectDeviceInfo() {
        infoMap.clear();
        try {
            PackageManager pm = myContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(myContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infoMap.put("versionName", versionName);
                infoMap.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            log.error("collect package info\n" + e.toString());
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infoMap.put(field.getName(), field.get(null).toString());
                log.error(field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                log.error("collect crash info\n" + e.toString());
            }
        }
    }
}