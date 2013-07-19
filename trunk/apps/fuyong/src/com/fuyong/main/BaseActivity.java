package com.fuyong.main;

import android.app.Activity;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-17
 * Time: 下午10:23
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends Activity {
    protected final Logger log = Log.getLogger(Log.MY_APP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.info("create activity: " + this.getClass().toString());
        ActivityManager.getInstance().addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        JPushInterface.activityStarted(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        JPushInterface.activityStopped(this);
    }

    @Override
    protected void onDestroy() {
        log.info("destroy activity: " + this.getClass().toString());
        ActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }
}
