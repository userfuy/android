package com.fuyong.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-7-19
 * Time: 下午10:47
 * To change this template use File | Settings | File Templates.
 */
public class JPushReceiver extends BroadcastReceiver {
    Logger log = Log.getLogger(Log.MY_APP);

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        log.debug("JPush receive " + intent.getAction());
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            log.debug("JPush message：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
            Toast.makeText(context
                    , bundle.getString(JPushInterface.EXTRA_MESSAGE)
                    , Toast.LENGTH_SHORT)
                    .show();
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            log.debug("JPush receive notification");
            // 在这里可以做些统计，或者做些其他工作
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            log.debug("JPush: user opened notification");
            // 在这里可以自己写代码去定义用户点击后的行为
            context.startActivity(new Intent("android.intent.action.MAIN"));
        } else {
            log.warn("JPush unhandled intent - " + intent.getAction());
        }
    }
}
