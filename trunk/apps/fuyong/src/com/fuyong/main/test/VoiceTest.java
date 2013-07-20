package com.fuyong.main.test;

import android.telephony.TelephonyManager;
import com.fuyong.main.MyScheduledThreadPool;
import com.fuyong.main.PhoneStateReceiver;
import com.fuyong.main.TelephonyUtil;
import org.dom4j.Element;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-23
 * Time: 下午4:45
 * To change this template use File | Settings | File Templates.
 */
public class VoiceTest extends Test implements Observer {

    private String number;
    private int callTime;
    private int count;
    private int interval;

    private boolean endCall;

    @Override
    public void config(Element element) {
        if (null == element) {
            return;
        }
        number = getStringValue(element.elementTextTrim("number"), "10086");
        callTime = Integer.parseInt(getStringValue(element.elementTextTrim("call-time"), "10"));
        count = Integer.parseInt(getStringValue(element.elementTextTrim("count"), "5"));
        interval = Integer.parseInt(getStringValue(element.elementTextTrim("test-interval"), "5"));
    }

    @Override
    public Object call() {
        try {
            log.info("begin voice test");
            PhoneStateReceiver.getInstance().addObserver(this);
            for (int i = 0; i < count; ++i) {
                log.info("test index: " + i);
                endCall = false;
                onStartCall();
                if (TelephonyUtil.call(number)) {
                    synchronized (this) {
                        wait(3000 * callTime);
                    }
                    //等待超时， 呼叫失败
                    if (!endCall) {
                        onCallFailed();
                    }
                } else {
                    onCallFailed();
                }
                Thread.sleep(1000 * interval);
            }
        } catch (InterruptedException e) {
            log.info("test interrupted");
            endCall();
        } catch (Exception e) {
            log.error(e.toString());
        } finally {
            PhoneStateReceiver.getInstance().deleteObserver(this);
            log.info("end voice test");
        }
        return null;
    }

    private void endCall() {
        TelephonyUtil.endCall();
        synchronized (this) {
            endCall = true;
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

    @Override
    public void update(Observable observable, Object data) {
        Integer state = (Integer) data;
        switch (state.intValue()) {
            case TelephonyManager.CALL_STATE_IDLE:
                log.info("call state: idle");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                log.info("call state: off hook");
                onCallEstablished();
                MyScheduledThreadPool.getExecutor().schedule(new Runnable() {
                    @Override
                    public void run() {
                        endCall();
                    }
                }
                        , callTime
                        , TimeUnit.SECONDS);
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                log.info("call state: ringing");
                onRinging();
                break;
        }
    }
}