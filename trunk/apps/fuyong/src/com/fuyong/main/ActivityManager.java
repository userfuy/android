package com.fuyong.main;

import android.app.Activity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: democrazy
 * Date: 13-6-17
 * Time: 下午10:14
 * To change this template use File | Settings | File Templates.
 */
public class ActivityManager {
    private Logger log = Log.getLogger(Log.MY_APP);
    private static ActivityManager instance;
    private List<Activity> activityList = new ArrayList<Activity>();

    private ActivityManager() {
    }

    synchronized public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    synchronized public static void release() {
        instance = null;
    }

    synchronized public void addActivity(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    synchronized public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    synchronized public void destroyAllActivity() {
        log.info("destroy all activities");
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}
