package com.fuyong.main.test;

import android.widget.Toast;
import com.fuyong.main.Log;
import com.fuyong.main.MyApp;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-23
 * Time: 下午4:35
 * To change this template use File | Settings | File Templates.
 */
public class TestManager {
    public static final int TEST_UNKNOWN = -1;
    public static final int TEST_IDLE = 0;
    public static final int TEST_RUN = 1;
    public static final int TEST_COMPLETE = 2;
    public static final int TEST_INTERRUPT = 3;

    private static TestManager instance;
    private Logger log = Log.getLogger(Log.MY_APP);
    private ExecutorService executorService;
    private int testState = TEST_IDLE;
    private Object testStateLock = new Object();
    private TestConfig testConfig = new TestConfig();

    private TestManager() {
    }

    synchronized public static TestManager getInstance() {
        if (null == instance) {
            instance = new TestManager();
        }
        return instance;
    }

    public void start(final String filePath) {
        synchronized (testStateLock) {
            if (TEST_RUN == testState) {
                Toast.makeText(MyApp.getInstance().getAppContext()
                        , "There is already a test,\nstop first before a new test."
                        , Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            testState = TEST_RUN;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                startTest(filePath);
            }
        }).start();
    }

    private void startTest(String filePath) {
        changeTestState(TEST_RUN);
        executorService = Executors.newSingleThreadExecutor();
        onTestStarted();
        log.info("begin test");
        if (!testConfig.load(filePath)) {
            changeTestState(TEST_IDLE);
            return;
        }
        List<Test> testList;
        testConfig.parse();
        testList = testConfig.getTestList();
        for (Test test : testList) {
            executorService.submit(test);
        }
        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                log.warn("awaitTermination time out: 60min");
            }
            onTestCompleted();
            changeTestState(TEST_COMPLETE);
        } catch (InterruptedException e) {
            changeTestState(TEST_INTERRUPT);
            onTestInterrupted();
            log.error(e.toString());
        }
    }

    private void changeTestState(int state) {
        synchronized (testStateLock) {
            testState = state;
        }
    }

    public void stop() {
        if (null == executorService) {
            return;
        }
        log.info("stop test");
        executorService.shutdownNow();
    }

    private void onTestStarted() {
        log.info("test started");
    }

    private void onTestInterrupted() {
        log.info("test interrupted");
    }

    private void onTestCompleted() {
        log.info("test completed");
    }
}
