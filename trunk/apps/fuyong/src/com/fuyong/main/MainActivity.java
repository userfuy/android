package com.fuyong.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import it.sauronsoftware.ftp4j.FTPClient;
import org.apache.log4j.Logger;

public class MainActivity extends BaseActivity {
    private Button startFtpBtn;
    private Button stopFtpBtn;
    private FTPClient ftpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        startFtpBtn = (Button) findViewById(R.id.start_ftp);
        startFtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        ftpClient = FTPUtil.makeFtpConnection("58.60.106.160", 21, "probe", "123");
                        if (null == ftpClient) {
                            return;
                        }
                        FTPUtil.upload(ftpClient, MyAppDirs.getAppRootDir() + "ftp/up.dat", "/up/fy", null);
                        FTPUtil.closeConnection(ftpClient);
                    }
                }.start();
            }
        });
        stopFtpBtn = (Button) findViewById(R.id.stop_ftp);
        stopFtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread() {
                    @Override
                    public void run() {
                        FTPUtil.abortDataTransfer(ftpClient, true);
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new MyToastThread("My app start.").start();
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

                        int j = 10 / 0;
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
