package com.fuyong.main;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
        InputStream inputStream = MyApp.getInstance().getResources().openRawResource(R.raw.test);
        try {
            String targetFilePath = MyAppDirs.getConfigDir() + "test/test_default.xml";
            FileUtil.createNewFile(targetFilePath);
            FileUtil.copyFile(inputStream, new FileOutputStream(targetFilePath));
        } catch (IOException e) {
            Log.exception(e);
        }
    }
}
