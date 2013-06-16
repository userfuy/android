package com.fuyong.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class MainActivity extends Activity {
    private Logger logger1 = Log.getLogger(Log.MY_APP);
    private Logger logger2 = Log.getLogger(Log.ERROR);

    @Override
    protected void onPause() {
        logger1.info("onPause");
        logger2.info("onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        logger1.info("onResume");
        logger2.info("onResume");
        super.onResume();
        bindService();
    }

    @Override
    protected void onStop() {
        logger1.info("onStop");
        logger2.info("onStop");
        unBindService();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        logger1.info("onDestroy");
        logger2.info("onDestroy");
        super.onDestroy();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger1.info("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getResources().openRawResource(R.raw.log4j);
    }

    private void bindService() {
        bindService(new Intent(this, MainService.class), sc, BIND_AUTO_CREATE);
    }

    private void unBindService() {
        unbindService(sc);
    }

    private MainService mainservice;
    ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            logger1.info("Connected MainService");
            mainservice = ((MainService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            logger1.info("Disconnected MainService");
            mainservice = null;
        }
    };
}
