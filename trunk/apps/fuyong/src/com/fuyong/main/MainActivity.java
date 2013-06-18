package com.fuyong.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import org.apache.log4j.Logger;

public class MainActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit)
                .setMessage(R.string.yes_to_exit)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityManager.getInstance().destroyAllActivity();
                        getApplication().onTerminate();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()
                .show();
    }

    @Override
    protected void onStop() {
        unBindService();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            log.info("connected MainService");
            mainservice = ((MainService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            log.info("disconnected MainService");
            mainservice = null;
        }
    };
}
