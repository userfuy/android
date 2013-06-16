package com.fuyong.main;

import android.os.Environment;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-16
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */
public class MyAppDirs {
    private final static String appRootDir = Environment.getExternalStorageDirectory()
            + File.separator + MyApp.getAppContext().getString(R.string.app_name) + File.separator;
    private final static String configDir = appRootDir + "config" + File.separator;
    private final static String userDataDir = appRootDir + "userData" + File.separator;
    private final static String logDir = appRootDir + "log" + File.separator;

    public static String getAppRootDir() {
        return appRootDir;
    }

    public static String getConfigDir() {
        return configDir;
    }

    public static String getUserDataDir() {
        return userDataDir;
    }

    public static String getLogDir() {
        return logDir;
    }
}
