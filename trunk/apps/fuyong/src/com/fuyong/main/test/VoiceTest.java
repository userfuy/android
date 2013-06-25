package com.fuyong.main.test;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.fuyong.main.MyScheduledThreadPool;
import com.fuyong.main.TelephonyUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-23
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public class VoiceTest extends Test {

    private String number = "10086";
    private int callTime = 10;
    private int count = 5;
    private int interval = 5;

    @Override
    public Object call() {
        try {
            log.info("start voice test");
            TelephonyUtil.getTelephonyManager().listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
            for (int i = 0; i < count; ++i) {
                log.info("test index: " + i);
                onStartCall();
                if (TelephonyUtil.call(number)) {
                    synchronized (this) {
                        wait(10000 * callTime);
                    }
                } else {
                    onCallFailed();
                }
                Thread.sleep(1000 * interval);
            }
        } catch (InterruptedException e) {
            log.error(e.toString());
        } finally {
            TelephonyUtil.getTelephonyManager().listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
            endCall();
        }
        return null;
    }

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    log.info("call state: idle");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    log.info("call state: off hook");
                    onCallEstablished();
                    if (null == incomingNumber || incomingNumber.isEmpty()) {
                        MyScheduledThreadPool.getExecutor().schedule(new Runnable() {
                            @Override
                            public void run() {
                                endCall();
                            }
                        }
                                , callTime
                                , TimeUnit.SECONDS);
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    log.info("call state: ringing");
                    onRinging();
                    break;
            }
        }
    };


    private void endCall() {
        TelephonyUtil.endCall();
        onEndCall();
        synchronized (this) {
            notifyAll();
        }
    }

    private void onStartCall() {
    }

    private void onRinging() {
    }

    private void onCallEstablished() {

    }

    private void onEndCall() {
    }

    private void onCallFailed() {

    }
}
