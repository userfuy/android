package com.fuyong.main;

import android.os.Environment;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-10
 * Time: 下午10:46
 * To change this template use File | Settings | File Templates.
 */
public class AppEnvironment extends Environment {

    public static void initAppEnvironment() {
        new File(MyAppDirs.getAppRootDir()).mkdirs();
//        InputStream inputStream = MyApp.getAppContext().getResources().openRawResource(R.raw.log4j);
//        try {
//            String targetFilePath = MyAppDirs.getConfigDir() + "log4j.xml";
//            FileUtil.createNewFile(targetFilePath);
//            FileUtil.copyFile(inputStream, new FileOutputStream(targetFilePath));
//        } catch (IOException e) {
//        }
    }
}
