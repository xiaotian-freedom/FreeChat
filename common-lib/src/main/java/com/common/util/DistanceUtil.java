package com.common.util;

import android.content.Context;
import android.text.TextUtils;

import com.common.R;

/**
 * Created by tianshutong on 16/8/5.
 */

public class DistanceUtil {

    /**
     * 转换km
     *
     * @param context
     * @param distance
     * @return
     */
    public static String getPoiStrDistanceInKm(Context context, double distance) {
        String strDistanceFormat = "";
        if (distance == -1) {
            return strDistanceFormat;
        }
        double distanceInKm = distance / 1000d;
        String strDistance = String.valueOf(distanceInKm);
        if (!TextUtils.isEmpty(strDistance)) {
            if (distance > 1000) {
                if (strDistance.contains(".")) {
                    int index = strDistance.indexOf(".");
                    strDistance = strDistance.substring(0, index);
                }
            } else if (distance > 100 && distance < 1000) {
                if (strDistance.length() > 3) {
                    strDistance = strDistance.substring(0, 3);
                }
            } else {
                if (strDistance.length() > 4) {
                    strDistance = strDistance.substring(0, 4);
                }
            }
            strDistanceFormat = context.getString(
                    R.string.map_near_company_distance, strDistance);
        }
        return strDistanceFormat;
    }

}
