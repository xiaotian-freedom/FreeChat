package com.common.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;

/**
 * Created by tianshutong on 16/7/16.
 */

public class SettingUtil {

    /**
     * 判断是否启动定位服务
     * @param context
     * @return
     */
    public static boolean isOpenLocService(Context context) {
        boolean isGps = false;//判断GPS是否启动
        boolean isNetwork = false;//判断网络定位是否启动
        if (context != null) {
            LocationManager locationManager = (LocationManager)
                    context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                isGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            if (isGps || isNetwork)
                return true;
        }
        return false;
    }

    /**
     * 跳转到系统设置定位界面
     * @param context
     */
    public static void goToLocationSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转到WIFI设置界面
     * @param context
     */
    public static void goToWifiSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
