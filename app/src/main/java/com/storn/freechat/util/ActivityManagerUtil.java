package com.storn.freechat.util;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Activity manager
 * Created by tianshutong on 2017/3/31.
 */

public class ActivityManagerUtil {

    private static LinkedList<Activity> allActivityList = new LinkedList<>();

    public static void addActivity(Activity act) {
        if (act != null && !act.isFinishing()) {
            allActivityList.add(act);
        }
    }

    public static void exitApp() {
        allActivityList.forEach((Activity activity) -> {
            if (activity != null && !activity.isFinishing())
                activity.finish();
        });
        System.exit(0);
    }
}
