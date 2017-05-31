package com.common.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by tianshutong on 16/7/16.
 */

public class MapUtil {

    /**
     * 打开高德Web地图
     *
     * @param centLatLon 初始地经纬度
     * @param destLatLon 目的地经纬度
     * @param from       初始地名
     * @param to         目的地名
     */
    public static void openAHttpMap(Context context, double[] centLatLon, double[] destLatLon, String from, String to) {
        try {
            StringBuilder loc = new StringBuilder();
            loc.append("http://m.amap.com/?from=");
            loc.append(centLatLon[0]);
            loc.append(",");
            loc.append(centLatLon[1]);
            loc.append("(");
            loc.append(from);
            loc.append(")");
            loc.append("&to=");
            loc.append(destLatLon[0]);
            loc.append(",");
            loc.append(destLatLon[1]);
            loc.append("(");
            loc.append(to);
            loc.append(")");
            loc.append("&type=2");
            Intent intent = Intent.getIntent(loc.toString());
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
