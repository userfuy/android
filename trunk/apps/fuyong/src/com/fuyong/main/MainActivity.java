package com.fuyong.main;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import com.fuyong.main.test.MyWebView;
import it.sauronsoftware.ftp4j.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends BaseActivity {
    private Button startFtpBtn;
    private Button stopFtpBtn;
    private Button startTestBtn;
    private Button stopTestBtn;
    private Button settingsBtn;
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

        startTestBtn = (Button) findViewById(R.id.start_test);
        startTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainservice.getTestManager().start(MyAppDirs.getConfigDir() + "test/test_default.xml");
            }
        });
        stopTestBtn = (Button) findViewById(R.id.stop_test);
        stopTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainservice.getTestManager().stop();
            }
        });
        settingsBtn = (Button) findViewById(R.id.settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        linearLayout.addView(MyWebView.getInstance().getWebView());
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
