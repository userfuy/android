package com.fuyong.main;

import android.app.Application;
import android.content.Context;
import cn.jpush.android.api.JPushInterface;
import com.fuyong.main.test.MyWebView;
import org.apache.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-10
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
public class MyApp extends Application {
    private static MyApp instance = null;

    public static MyApp getInstance() {
        return instance;
    }

    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initJPush();
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.getInstance());
        if (!FileUtil.isExit(MyAppDirs.getAppRootDir())) {
            AppEnvironment.initAppEnvironment();
        }

        Log.init();
        Logger log = Log.getLogger(Log.MY_APP);
        log.info("\n############################################\n" +
                "############                   #############\n" +
                "############ Start Application #############\n" +
                "############                   #############\n" +
                "############################################\n");
        MyWebView.getInstance();
        PhoneStateReceiver.getInstance();
    }

    private void initJPush() {
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        Set<String> tags = new LinkedHashSet<String>();
        tags.add("debug");
        JPushInterface.setAliasAndTags(this, "debug", tags);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger log = Log.getLogger(Log.MY_APP);
        log.info("\n############################################\n" +
                "############                   #############\n" +
                "############ Exit Application  #############\n" +
                "############                   #############\n" +
                "############################################\n");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger log = Log.getLogger(Log.MY_APP);
        log.warn("low memory");
    }
}
