package com.storn.freechat.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.content.Context.ACTIVITY_SERVICE;

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

    public static void removeActivity(Activity act) {
        if (act != null && !act.isFinishing()) {
            allActivityList.remove(act);
        }
    }

    public static void exitApp() {
        allActivityList.forEach((Activity activity) -> {
            if (activity != null && !activity.isFinishing())
                activity.finish();
        });
        System.exit(0);
    }

    public static boolean getTopActivty(Context context, String className) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            // We get usage stats for the last 10 seconds
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (!mySortedMap.isEmpty()) {
                    String topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    if (topPackageName.equals(className)) {
                        return true;
                    }
                }
            }
        } else {
            ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
            String cmpNameTemp = null;
            if (runningTasks != null) {
                cmpNameTemp = runningTasks.get(0).topActivity.toString();
            }
            if (cmpNameTemp == null) {
                return false;
            }
            if (cmpNameTemp.equals(className)) {
                return true;
            }
        }

        return false;
    }
}
