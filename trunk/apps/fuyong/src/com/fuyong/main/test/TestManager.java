package com.fuyong.main.test;

import android.widget.Toast;
import com.fuyong.main.Log;
import com.fuyong.main.MyApp;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-23
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
public class TestManager {
    private static TestManager instance;
    private Logger log = Log.getLogger(Log.MY_APP);
    private ExecutorService executorService;

    private TestManager() {
    }

    synchronized public static TestManager getInstance() {
        if (null == instance) {
            instance = new TestManager();
        }
        return instance;
    }

    synchronized public void start() {
        if (null != executorService && !executorService.isTerminated()) {
            Toast.makeText(MyApp.getInstance().getAppContext()
                    , "There is already a test,\nstop first before a new test."
                    , Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        executorService = Executors.newSingleThreadExecutor();
        onTestStarted();
        //提交任务
        executorService.execute(new FutureTask<Object>(new VoiceTest()));
        //shutdown后等待提交任务执行完毕
        executorService.shutdown();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!executorService.awaitTermination(1, TimeUnit.HOURS)) {
                        log.warn("awaitTermination time out: 1hour");
                    }
                    onTestCompleted();
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        }).start();
    }


    synchronized public void stop() {
        log.info("stop test");
        if (null == executorService)
            return;
        executorService.shutdownNow();
    }

    private void onTestStarted() {
        log.info("test started");
    }

    private void onTestStopped() {

    }

    private void onTestCompleted() {
        log.info("test completed");
    }
}
