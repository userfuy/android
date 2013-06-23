package com.fuyong.main;

import android.os.Looper;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-19
 * Time: 下午10:21
 * To change this template use File | Settings | File Templates.
 */
public class MyToastThread extends Thread {
    private String msg;

    public MyToastThread(String msg) {
        super();
        this.msg = msg;
    }

    @Override
    public void run() {

        Looper.prepare();
        Toast.makeText(MyApp.getInstance().getAppContext(), msg, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
