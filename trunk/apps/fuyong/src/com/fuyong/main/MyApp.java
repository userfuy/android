package com.fuyong.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (!FileUtil.isExit(MyAppDirs.getAppRootDir())) {
            AppEnvironment.initAppEnvironment();
        }
        Log.init();
    }
}
