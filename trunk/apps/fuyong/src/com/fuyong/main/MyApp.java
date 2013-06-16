package com.fuyong.main;

import android.app.Application;
import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-10
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
public class MyApp extends Application {
    private static MyApp instance = null;
    private static final String APP_SETTINGS = "APP_SETTINGS";

    public static Context getAppContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler.getInstance());
        if (!FileUtil.isExit(MyAppDirs.getAppRootDir())) {
            AppEnvironment.initAppEnvironment();
        }
        Log.init();
    }
}
