package com.fuyong.main;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import org.apache.log4j.Logger;

public class MainActivity extends Activity {
    private Logger log = Log.getLogger(Log.MY_APP);

    @Override
    protected void onPause() {
        log.info("onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        log.info("onResume");
        super.onResume();
        bindService();
    }

    @Override
    protected void onStop() {
        log.info("onStop");
        unBindService();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        log.info("onDestroy");
        super.onDestroy();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.info("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
            log.info("Connected MainService");
            mainservice = ((MainService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            log.info("Disconnected MainService");
            mainservice = null;
        }
    };
}
