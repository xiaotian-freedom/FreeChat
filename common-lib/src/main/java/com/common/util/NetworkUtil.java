package com.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络工具类
 * Created by Stefen on 2016/5/24.
 */
public class NetworkUtil {

    /**
     * Whether network is connected
     *
     * @param context Context
     * @return a boolean {@code true} if the network is connected, else
     * {@code false}
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return (ni != null) && (ni.isConnectedOrConnecting());
        }

        return false;
    }

}
