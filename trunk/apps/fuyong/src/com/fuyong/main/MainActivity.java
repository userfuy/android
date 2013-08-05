package com.fuyong.main;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.fuyong.main.test.MyWebView;
import it.sauronsoftware.ftp4j.FTPClient;

public class MainActivity extends BaseActivity {
    private Button ftpBtn;
    private boolean ftpStarted = false;
    private Button testBtn;
    private boolean testStarted = false;
    private Button httpDownloadBtn;
    private boolean httpStarted = false;
    private Button settingsBtn;

    private FTPClient ftpClient;
    private LinearLayout linearLayout;
    private HttpDownload httpDownload;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ftpBtn = (Button) findViewById(R.id.ftp_btn);
        ftpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ftpStarted) {
                    FTPUtil.abortDataTransfer(ftpClient, true);
                } else {
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
                ftpStarted = !ftpStarted;
                if (ftpStarted) {
                    ftpBtn.setText("stop ftp");
                } else {
                    ftpBtn.setText("start ftp");
                }
            }
        });

        testBtn = (Button) findViewById(R.id.test_btn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (testStarted) {
                    mainservice.getTestManager().stop();
                } else {
                    mainservice.getTestManager().start(MyAppDirs.getConfigDir() + "test/test_default.xml");
                }
                testStarted = !testStarted;
                if (testStarted) {
                    testBtn.setText("stop test");
                } else {
                    testBtn.setText("start test");
                }
            }
        });
        httpDownloadBtn = (Button) findViewById(R.id.http_download_btn);
        httpDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (httpStarted) {
                    httpDownload.stop();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            httpDownload = new HttpDownload("http://gdown.baidu.com/data/wisegame/c0fd884eec5df4e2/shenmiaotaowang2_115.apk", MyAppDirs.getAppRootDir() + "/down/", 4);
//                            httpDownload = new HttpDownload(null, MyAppDirs.getAppRootDir() + "/down/", 4);
                            try {
                                httpDownload.download(false);
                            } catch (InterruptedException e) {

                            }
                        }
                    }.start();
                }
                httpStarted = !httpStarted;
                if (httpStarted) {
                    httpDownloadBtn.setText("stop http download");
                } else {
                    httpDownloadBtn.setText("start http download");
                }
            }
        });
        settingsBtn = (Button) findViewById(R.id.settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
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
        linearLayout.removeView(MyWebView.getInstance().getWebView());
        getApplication().onTerminate();
    }

    private void bindService() {
        bindService(new Intent(this, MainService.class), mainsc, BIND_AUTO_CREATE);
    }

    private void unBindService() {
        unbindService(mainsc);
    }

    private MainService mainservice;
    ServiceConnection mainsc = new ServiceConnection() {
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
    private PhoneService phoneService;
    ServiceConnection phonesc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            log.info("connected PhoneService");
            phoneService = ((PhoneService.MyBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            log.info("disconnected PhoneService");
            phoneService = null;
        }
    };
}
