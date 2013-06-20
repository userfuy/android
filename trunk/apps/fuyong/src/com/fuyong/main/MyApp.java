package com.fuyong.main;

import android.app.Application;
import android.content.Context;
import org.apache.log4j.Logger;

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
        Log.init();
        Logger log = Log.getLogger(Log.MY_APP);
        log.warn("low memory");
    }
}
